package com.event.producer;

import com.event.events.PedidoCriadoNotificationEvent;
import com.event.model.Pedido;
import com.event.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PedidoRepository pedidoRepository;

    @Value("${kafka.topics.notification}")
    private String topic;

    public NotificationProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                PedidoRepository pedidoRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido criarPedidoENotificar(String cliente, double valor, List<String> itens) {
        // 1. Salva os dados completos no "banco"
        Pedido pedido = pedidoRepository.salvar(cliente, valor, itens);

        // 2. Publica NO KAFKA apenas o ID — sem dados extras
        var evento = new PedidoCriadoNotificationEvent(pedido.id());

        kafkaTemplate.send(topic, String.valueOf(pedido.id()), evento);

        log.info("[NOTIFICATION - PRODUCER] Publicado no tópico '{}': {}", topic, evento);
        log.info("[NOTIFICATION - PRODUCER] Dados completos estão no banco, ID={}", pedido.id());

        return pedido;
    }
}