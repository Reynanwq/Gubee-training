package com.event.producer;

import com.event.events.PedidoCriadoCarriedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarriedDataProducer {

    private static final Logger log = LoggerFactory.getLogger(CarriedDataProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.carried-data}")
    private String topic;

    public CarriedDataProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void criarPedidoEPublicar(Long pedidoId, String cliente, double valor, List<String> itens) {
        // Publica NO KAFKA com TODOS os dados — o consumer não precisa buscar nada
        var evento = new PedidoCriadoCarriedEvent(pedidoId, cliente, valor, itens);

        kafkaTemplate.send(topic, String.valueOf(pedidoId), evento);

        log.info("[CARRIED DATA - PRODUCER] Publicado no tópico '{}' com dados completos: {}", topic, evento);
    }
}