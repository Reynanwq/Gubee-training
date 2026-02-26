package com.example.kafka.pattern2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * PATTERN 2 - Dead Letter Queue (DLQ)
 *
 * Events that cannot be processed are routed to an error topic (DLQ).
 * The main stream continues normally without stopping.
 * There is NO retry — an event either succeeds or goes to the DLQ.
 *
 * Use case: Events with invalid format or missing required fields.
 *
 * Note: Spring Kafka also supports automatic DLQ routing via
 * DeadLetterPublishingRecoverer + DefaultErrorHandler, but here we do it
 * manually so the logic is explicit and easy to understand.
 */
@Component
public class Pattern2Consumer {

    private static final Logger log = LoggerFactory.getLogger(Pattern2Consumer.class);

    private final Pattern2Producer producer;

    public Pattern2Consumer(Pattern2Producer producer) {
        this.producer = producer;
    }

    @KafkaListener(
        topics = "${kafka.topics.p2-source}",
        groupId = "p2-group",
        id = "p2-listener"
    )
    public void consume(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("[P2] Received message at offset {}: {}", offset, message);

        try {
            String processed = process(message);
            producer.sendToTarget(processed);
            log.info("[P2] Successfully processed: {}", processed);

        } catch (InvalidMessageException e) {
            // Route to DLQ — main consumer continues normally
            log.warn("[P2] Invalid message, routing to DLQ. Reason: {}", e.getMessage());
            producer.sendToError(message, e.getMessage());
        }
    }

    private String process(String message) {
        // Simulate validation: messages must start with "ORDER:"
        if (!message.startsWith("ORDER:")) {
            throw new InvalidMessageException("Expected format 'ORDER:<data>', got: " + message);
        }
        return "PROCESSED: " + message;
    }

    static class InvalidMessageException extends RuntimeException {
        public InvalidMessageException(String msg) { super(msg); }
    }
}
