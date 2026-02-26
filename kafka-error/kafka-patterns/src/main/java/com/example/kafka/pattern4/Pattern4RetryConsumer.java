package com.example.kafka.pattern4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * PATTERN 4 - Retry Consumer
 *
 * Processes events from the retry topic. When an event is successfully
 * processed, it publishes a tombstone to the redirect topic so the
 * main consumer can clean up the OrderedRetryStore.
 *
 * Events for the same item are processed in the order they arrived,
 * because the retry topic preserves insertion order.
 */
@Component
public class Pattern4RetryConsumer {

    private static final Logger log = LoggerFactory.getLogger(Pattern4RetryConsumer.class);

    private static final int MAX_RETRY_ATTEMPTS = 5;

    private final Pattern4Producer producer;
    private final Pattern4PriceService priceService;

    public Pattern4RetryConsumer(Pattern4Producer producer, Pattern4PriceService priceService) {
        this.producer = producer;
        this.priceService = priceService;
    }

    @KafkaListener(
        topics = "${kafka.topics.p4-retry}",
        groupId = "p4-retry-group",
        id = "p4-retry-listener"
    )
    public void consume(
        @Payload String message,
        @Header(KafkaHeaders.OFFSET) long offset,
        @Header(value = "message-id", defaultValue = "") String messageId,
        @Header(value = "item-id", defaultValue = "") String itemId,
        @Header(value = "retry-count", defaultValue = "0") int retryCount
    ) {
        log.info("[P4-Retry] Attempt {} for messageId={}, itemId={}, offset={}", retryCount + 1, messageId, itemId, offset);

        if (retryCount >= MAX_RETRY_ATTEMPTS) {
            log.error("[P4-Retry] Max retries exceeded for messageId={} → DLQ", messageId);
            producer.sendToError(message, "Max retries exceeded");
            // Still publish tombstone so the main consumer unblocks this item
            producer.sendTombstone(messageId, itemId);
            return;
        }

        String extractedItemId = itemId.isEmpty() ? extractItemId(message) : itemId;

        if (!priceService.isPriceAvailable(extractedItemId)) {
            log.info("[P4-Retry] Price still not available for '{}' → back to retry", extractedItemId);
            producer.sendToRetry(message, messageId, extractedItemId, retryCount + 1);
            return;
        }

        // Success — process and notify the main consumer via tombstone
        String processed = process(message, priceService.getPrice(extractedItemId));
        producer.sendToTarget(processed);
        log.info("[P4-Retry] Successfully processed: {}", processed);

        // Publish tombstone to redirect topic to unblock subsequent events for this item
        producer.sendTombstone(messageId, extractedItemId);
    }

    private String extractItemId(String message) {
        String[] parts = message.split(":");
        return parts.length > 1 ? parts[1] : "UNKNOWN";
    }

    private String process(String message, double price) {
        return "RETRIED[price=" + price + "]: " + message;
    }
}
