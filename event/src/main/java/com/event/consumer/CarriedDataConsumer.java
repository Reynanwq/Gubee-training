package com.event.consumer;

import com.event.events.PedidoCriadoCarriedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CarriedDataConsumer {

    private static final Logger log = LoggerFactory.getLogger(CarriedDataConsumer.class);

    @KafkaListener(
            topics = "${kafka.topics.carried-data}",
            groupId = "carried-data-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumir(PedidoCriadoCarriedEvent evento) {
        log.info("[CARRIED DATA - CONSUMER] Evento recebido com dados completos:");
        log.info("  → pedidoId : {}", evento.pedidoId());
        log.info("  → cliente  : {}", evento.cliente());
        log.info("  → valor    : R$ {}", evento.valor());
        log.info("  → itens    : {}", evento.itens());
        log.info("[CARRIED DATA - CONSUMER] Nenhuma chamada externa necessária!");

        // Pode processar tudo aqui: baixar estoque, enviar e-mail, emitir NF, etc.
        processarEstoque(evento);
        processarEmail(evento);
    }

    private void processarEstoque(PedidoCriadoCarriedEvent evento) {
        log.info("[CARRIED DATA - CONSUMER] [Estoque] Baixando itens: {}", evento.itens());
    }

    private void processarEmail(PedidoCriadoCarriedEvent evento) {
        log.info("[CARRIED DATA - CONSUMER] [E-mail] Enviando confirmação para cliente: {}", evento.cliente());
    }
}