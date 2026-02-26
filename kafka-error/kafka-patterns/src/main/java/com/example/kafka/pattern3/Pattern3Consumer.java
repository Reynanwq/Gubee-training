package com.example.kafka.pattern3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * PATTERN 3 - Retry Topic
 *
 * Three possible paths for each event:
 *  1. Normal: event is processed and published to target
 *  2. Invalid: event goes to the error (DLQ) topic — not retried
 *  3. Recoverable: dependent data is missing (e.g. price not available yet)
 *     → event goes to retry topic and will be retried later
 *
 * ⚠️ Important: ORDER IS NOT GUARANTEED.
 * Events going through the retry path may arrive at the target AFTER
 * events that were received later but processed immediately.
 *
 * Use this pattern only when out-of-order processing is acceptable.
 */
@Component
public class Pattern3Consumer {

    private static final Logger log = LoggerFactory.getLogger(Pattern3Consumer.class);

    private final Pattern3Producer producer;
    private final PriceService priceService;

    public Pattern3Consumer(Pattern3Producer producer, PriceService priceService) {
        this.producer = producer;
        this.priceService = priceService;
    }

    @KafkaListener(
        topics = "${kafka.topics.p3-source}",
        groupId = "p3-group",
        id = "p3-main-listener"
    )
    public void consume(
        @Payload String message,
        @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("[P3-Main] Received at offset {}: {}", offset, message);
        route(message);
    }

    void route(String message) {
        try {
            validate(message);
        } catch (InvalidMessageException e) {
            log.warn("[P3-Main] Invalid message → DLQ. Reason: {}", e.getMessage());
            producer.sendToError(message, e.getMessage());
            return;
        }

        String itemId = extractItemId(message);
        if (!priceService.isPriceAvailable(itemId)) {
            // Recoverable condition: price not available yet → retry later
            log.info("[P3-Main] Price not available for item '{}' → retry topic", itemId);
            producer.sendToRetry(message);
            return;
        }

        String processed = process(message, priceService.getPrice(itemId));
        producer.sendToTarget(processed);
        log.info("[P3-Main] Processed normally: {}", processed);
    }

    private void validate(String message) {
        if (!message.startsWith("ORDER:")) {
            throw new InvalidMessageException("Invalid format: " + message);
        }
    }

    private String extractItemId(String message) {
        // Expected format: "ORDER:ITEM_ID:quantity"
        String[] parts = message.split(":");
        return parts.length > 1 ? parts[1] : "UNKNOWN";
    }

    private String process(String message, double price) {
        return "PROCESSED[price=" + price + "]: " + message;
    }

    static class InvalidMessageException extends RuntimeException {
        public InvalidMessageException(String msg) { super(msg); }
    }
}
