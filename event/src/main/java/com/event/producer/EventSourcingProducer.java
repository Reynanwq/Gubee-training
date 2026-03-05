package com.event.producer;


import com.event.events.conta.ContaCriadaEvento;
import com.event.events.conta.ContaEvento;
import com.event.events.conta.DepositoEvento;
import com.event.events.conta.SaqueEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventSourcingProducer {

    private static final Logger log = LoggerFactory.getLogger(EventSourcingProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.event-sourcing}")
    private String topic;

    public EventSourcingProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void criarConta(Long contaId) {
        publicar(contaId, new ContaCriadaEvento(contaId));
    }

    public void depositar(Long contaId, double valor) {
        publicar(contaId, new DepositoEvento(contaId, valor));
    }

    public void sacar(Long contaId, double valor) {
        publicar(contaId, new SaqueEvento(contaId, valor));
    }

    private void publicar(Long contaId, ContaEvento evento) {
        // A chave é o contaId → garante ordem dos eventos por conta na mesma partição
        kafkaTemplate.send(topic, String.valueOf(contaId), evento);
        log.info("[EVENT SOURCING - PRODUCER] Gravado no tópico '{}': {}", topic, evento);
    }
}