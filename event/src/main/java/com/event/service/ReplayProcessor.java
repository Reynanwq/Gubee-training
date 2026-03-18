package com.event.service;

import com.event.events.conta.ContaCriadaEvento;
import com.event.events.conta.ContaEvento;
import com.event.events.conta.DepositoEvento;
import com.event.events.conta.SaqueEvento;
import com.event.model.ContaBancaria;
import com.event.model.ContaPontoNoTempoEntity;
import com.event.repository.ContaPontoNoTempoRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class ReplayProcessor {

    private static final Logger log = LoggerFactory.getLogger(ReplayProcessor.class);

    private final ContaPontoNoTempoRepository pontoNoTempoRepository;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.event-sourcing}")
    private String topic;

    public ReplayProcessor(ContaPontoNoTempoRepository pontoNoTempoRepository) {
        this.pontoNoTempoRepository = pontoNoTempoRepository;
    }

    /**
     * Re-executa todos os eventos de uma conta desde o início do tópico
     * até o timestamp informado, reconstruindo o estado naquele momento.
     * O resultado é salvo no Point in Time DB (tabela conta_ponto_no_tempo).
     *
     * @param contaId       ID da conta a reconstruir
     * @param pontoNoTempo  Momento até onde os eventos devem ser reprocessados
     * @return Snapshot do estado da conta naquele momento
     */
    public ContaPontoNoTempoEntity executarReplay(Long contaId, Instant pontoNoTempo) {
        log.info("[REPLAY PROCESSOR] Iniciando replay da conta {} até {}", contaId, pontoNoTempo);

        // Estado que será reconstruído evento a evento
        ContaBancaria estadoReconstruido = new ContaBancaria(contaId);
        long timestampLimite = pontoNoTempo.toEpochMilli();

        // Cria um consumer Kafka dedicado ao replay (não interfere no consumer principal)
        try (KafkaConsumer<String, Object> replayConsumer = criarReplayConsumer()) {

            TopicPartition particao = new TopicPartition(topic, 0);
            replayConsumer.assign(List.of(particao));

            // Começa do offset 0 — lê TODOS os eventos desde o início
            replayConsumer.seekToBeginning(List.of(particao));

            boolean continuar = true;
            while (continuar) {
                ConsumerRecords<String, Object> registros = replayConsumer.poll(Duration.ofSeconds(3));

                // Se não há mais registros, encerra
                if (registros.isEmpty()) break;

                for (ConsumerRecord<String, Object> registro : registros) {

                    // Para quando ultrapassar o timestamp limite
                    if (registro.timestamp() > timestampLimite) {
                        continuar = false;
                        break;
                    }

                    // Processa apenas eventos da conta solicitada
                    if (!String.valueOf(contaId).equals(registro.key())) continue;

                    ContaEvento evento = converterEvento(registro.value());
                    if (evento == null) continue;

                    // Aplica o evento no estado reconstruído
                    aplicarEvento(estadoReconstruido, evento);

                    log.info("[REPLAY PROCESSOR] Evento aplicado: {} | Saldo parcial: R$ {}",
                            evento, estadoReconstruido.getSaldo());
                }
            }
        }

        // Salva o resultado no Point in Time DB
        ContaPontoNoTempoEntity snapshot = new ContaPontoNoTempoEntity(
                contaId,
                estadoReconstruido.getSaldo(),
                estadoReconstruido.getTotalEventos(),
                pontoNoTempo
        );
        pontoNoTempoRepository.save(snapshot);

        log.info("[REPLAY PROCESSOR] Replay concluído. Conta {} | Saldo em {}: R$ {}",
                contaId, pontoNoTempo, estadoReconstruido.getSaldo());

        return snapshot;
    }

    /**
     * Aplica um evento ao estado reconstruído em memória.
     */
    private void aplicarEvento(ContaBancaria conta, ContaEvento evento) {
        switch (evento) {
            case ContaCriadaEvento e -> conta.inicializar();
            case DepositoEvento e    -> conta.depositar(e.valor());
            case SaqueEvento e       -> conta.sacar(e.valor());
        }
    }

    /**
     * Converte o valor bruto do registro Kafka para ContaEvento.
     * O JsonDeserializer já desserializa usando o header de tipo — apenas fazemos o cast seguro.
     */
    private ContaEvento converterEvento(Object value) {
        if (value instanceof ContaEvento evento) return evento;
        log.warn("[REPLAY PROCESSOR] Não foi possível converter evento: {}", value);
        return null;
    }

    /**
     * Cria um KafkaConsumer isolado para uso exclusivo do replay.
     * Usa grupo único para não afetar o offset do consumer principal.
     */
    private KafkaConsumer<String, Object> criarReplayConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "replay-processor-" + System.currentTimeMillis());
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, "true");
        return new KafkaConsumer<>(props);
    }
}