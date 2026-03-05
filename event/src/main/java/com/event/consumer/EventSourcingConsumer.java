package com.event.consumer;

import com.event.events.conta.ContaCriadaEvento;
import com.event.events.conta.ContaEvento;
import com.event.events.conta.DepositoEvento;
import com.event.events.conta.SaqueEvento;
import com.event.model.ContaBancaria;
import com.event.model.ContaBancariaEntity;
import com.event.repository.ContaBancariaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventSourcingConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventSourcingConsumer.class);

    // Estado atual em memória (leitura rápida — espelho do Current State DB)
    private final Map<Long, ContaBancaria> contas = new ConcurrentHashMap<>();

    // Current State DB — persiste o estado reconstruído após cada evento
    private final ContaBancariaRepository repository;

    public EventSourcingConsumer(ContaBancariaRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "${kafka.topics.event-sourcing}",
            groupId = "event-sourcing-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumir(ContaEvento evento) {
        log.info("[EVENT SOURCING - CONSUMER] Evento recebido: {}", evento);

        switch (evento) {
            case ContaCriadaEvento e -> {
                // Atualiza estado em memória
                var conta = new ContaBancaria(e.contaId());
                conta.inicializar();
                contas.put(e.contaId(), conta);

                // Persiste no Current State DB
                var entity = new ContaBancariaEntity(e.contaId());
                entity.inicializar();
                repository.save(entity);

                log.info("[EVENT SOURCING - CONSUMER] Conta {} criada e salva no DB. Saldo: R$ 0,00", e.contaId());
            }

            case DepositoEvento e -> {
                // Atualiza estado em memória
                ContaBancaria conta = contas.computeIfAbsent(e.contaId(), ContaBancaria::new);
                conta.depositar(e.valor());

                // Busca do Current State DB, aplica o evento e salva
                ContaBancariaEntity entity = repository.findById(e.contaId())
                        .orElseGet(() -> new ContaBancariaEntity(e.contaId()));
                entity.depositar(e.valor());
                repository.save(entity);

                log.info("[EVENT SOURCING - CONSUMER] Depósito de R$ {} na conta {}. Novo saldo: R$ {}",
                        e.valor(), e.contaId(), conta.getSaldo());
            }

            case SaqueEvento e -> {
                // Atualiza estado em memória
                ContaBancaria conta = contas.computeIfAbsent(e.contaId(), ContaBancaria::new);
                conta.sacar(e.valor());

                // Busca do Current State DB, aplica o evento e salva
                ContaBancariaEntity entity = repository.findById(e.contaId())
                        .orElseGet(() -> new ContaBancariaEntity(e.contaId()));
                entity.sacar(e.valor());
                repository.save(entity);

                log.info("[EVENT SOURCING - CONSUMER] Saque de R$ {} na conta {}. Novo saldo: R$ {}",
                        e.valor(), e.contaId(), conta.getSaldo());
            }
        }
    }

    public Map<Long, ContaBancaria> getContas() {
        return contas;
    }
}