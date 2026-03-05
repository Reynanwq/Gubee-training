package com.event.consumer;

import com.event.events.PedidoCriadoNotificationEvent;
import com.event.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final PedidoRepository pedidoRepository;

    public NotificationConsumer(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @KafkaListener(
            topics = "${kafka.topics.notification}",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumir(PedidoCriadoNotificationEvent evento) {
        log.info("[NOTIFICATION - CONSUMER] Evento recebido: apenas ID={}", evento.pedidoId());

        // O consumer precisa ir buscar os dados — acoplado à fonte!
        pedidoRepository.buscarPorId(evento.pedidoId()).ifPresentOrElse(
                pedido -> log.info("[NOTIFICATION - CONSUMER] Dados buscados do banco: {}", pedido),
                ()     -> log.warn("[NOTIFICATION - CONSUMER] Pedido ID={} não encontrado!", evento.pedidoId())
        );
    }
}
