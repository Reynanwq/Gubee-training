package com.example.kafka.pattern1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * PATTERN 1 - Stop on Error
 *
 * Todos os eventos DEVEM ser processados em ordem, sem exceções.
 * Se qualquer evento falhar, o consumer para e aguarda intervenção manual.
 *
 * O retry infinito é configurado no application.yml:
 *   spring.kafka.listener.backoff.max-attempts: -1  (ilimitado)
 *   spring.kafka.listener.backoff.delay: 5000       (5s entre tentativas)
 */
@Component
public class Pattern1Consumer {

    private static final Logger log = LoggerFactory.getLogger(Pattern1Consumer.class);

    private final Pattern1Producer producer;

    public Pattern1Consumer(Pattern1Producer producer) {
        this.producer = producer;
    }

    @KafkaListener(
            topics = "${kafka.topics.p1-source}",
            groupId = "p1-group",
            id = "p1-listener"
    )
    public void consume(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("[P1] Received message at offset {}: {}", offset, message);

        String processed = process(message);

        producer.sendToTarget(processed);
        log.info("[P1] Successfully processed and forwarded: {}", processed);
    }

    private String process(String message) {
        if (message.contains("FAIL")) {
            log.error("[P1] Processing failed for message: {}. Blocking partition until fixed!", message);
            throw new RuntimeException("Critical processing failure: " + message);
        }
        return "PROCESSED: " + message;
    }
}