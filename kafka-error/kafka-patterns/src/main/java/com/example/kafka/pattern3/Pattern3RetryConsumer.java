package com.example.kafka.pattern3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * PATTERN 3 - Retry Consumer
 *
 * A dedicated consumer (usually fewer instances) that periodically
 * re-attempts processing of events that had missing dependencies.
 *
 * The retry consumer reuses the same routing logic from Pattern3Consumer.
 * If the dependency is still not available, the event goes back to the
 * retry topic (or error topic after max attempts).
 */
@Component
public class Pattern3RetryConsumer {

    private static final Logger log = LoggerFactory.getLogger(Pattern3RetryConsumer.class);

    private static final int MAX_RETRY_ATTEMPTS = 5;

    private final Pattern3Consumer mainConsumer;
    private final Pattern3Producer producer;

    public Pattern3RetryConsumer(Pattern3Consumer mainConsumer, Pattern3Producer producer) {
        this.mainConsumer = mainConsumer;
        this.producer = producer;
    }

    @KafkaListener(
        topics = "${kafka.topics.p3-retry}",
        groupId = "p3-retry-group",
        id = "p3-retry-listener"
    )
    public void consume(
        @Payload String message,
        @Header(KafkaHeaders.OFFSET) long offset,
        @Header(value = "retry-count", defaultValue = "0") int retryCount
    ) {
        log.info("[P3-Retry] Retrying (attempt {}) message at offset {}: {}", retryCount + 1, offset, message);

        if (retryCount >= MAX_RETRY_ATTEMPTS) {
            log.error("[P3-Retry] Max retries ({}) exceeded for message: {} → DLQ", MAX_RETRY_ATTEMPTS, message);
            producer.sendToError(message, "Max retry attempts exceeded");
            return;
        }

        // Delegate back to the main routing logic.
        // If price is now available, it will go to target. Otherwise, back to retry.
        mainConsumer.route(message);
    }
}
