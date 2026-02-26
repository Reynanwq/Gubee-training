package com.example.kafka.pattern4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * PATTERN 4 - Ordered Retry
 *
 * Same 3 paths as Pattern 3 (normal, error, retry), but ORDER IS PRESERVED.
 *
 * Key idea: if any event for an item is currently in the retry path,
 * ALL subsequent events for that same item must also go through retry,
 * to preserve the original sequence.
 *
 * Mechanism:
 * 1. When an event needs retry, its unique ID is stored in the OrderedRetryStore
 *    (grouped by itemId) and published to the redirect topic.
 * 2. For every new incoming event, the store is checked first.
 *    If the item has ongoing retries → send directly to retry (skip processing).
 * 3. When the retry consumer successfully processes an event, it publishes
 *    a tombstone (null value) to the redirect topic.
 * 4. The main consumer listens to the redirect topic and removes the completed
 *    message ID from the store. Once the store is empty for that item,
 *    subsequent events can follow the normal path again.
 */
@Component
public class Pattern4Consumer {

    private static final Logger log = LoggerFactory.getLogger(Pattern4Consumer.class);

    private final Pattern4Producer producer;
    private final OrderedRetryStore retryStore;
    private final Pattern4PriceService priceService;

    public Pattern4Consumer(Pattern4Producer producer,
                            OrderedRetryStore retryStore,
                            Pattern4PriceService priceService) {
        this.producer = producer;
        this.retryStore = retryStore;
        this.priceService = priceService;
    }

    // -------------------------------------------------------------------------
    // Main source listener
    // -------------------------------------------------------------------------
    @KafkaListener(
        topics = "${kafka.topics.p4-source}",
        groupId = "p4-group",
        id = "p4-main-listener"
    )
    public void consumeSource(
        @Payload String message,
        @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("[P4-Main] Received at offset {}: {}", offset, message);

        String messageId = UUID.randomUUID().toString();
        String itemId = extractItemId(message);

        try {
            validate(message);
        } catch (Exception e) {
            log.warn("[P4-Main] Invalid message → DLQ: {}", e.getMessage());
            producer.sendToError(message, e.getMessage());
            return;
        }

        // If this item already has events in the retry path, we MUST send
        // this event to retry too — otherwise we'd process it out of order.
        if (retryStore.isRetrying(itemId)) {
            log.info("[P4-Main] Item '{}' has ongoing retries → queuing this event to retry too", itemId);
            sendToRetryPath(message, messageId, itemId);
            return;
        }

        if (!priceService.isPriceAvailable(itemId)) {
            log.info("[P4-Main] Price not available for '{}' → retry path", itemId);
            sendToRetryPath(message, messageId, itemId);
            return;
        }

        // Normal flow
        String processed = process(message, priceService.getPrice(itemId));
        producer.sendToTarget(processed);
        log.info("[P4-Main] Processed normally: {}", processed);
    }

    // -------------------------------------------------------------------------
    // Redirect topic listener — receives tombstones (null) when retry succeeds
    // -------------------------------------------------------------------------
    @KafkaListener(
        topics = "${kafka.topics.p4-redirect}",
        groupId = "p4-redirect-group",
        id = "p4-redirect-listener"
    )
    public void consumeRedirect(
        @Payload(required = false) String payload,
        @Header(value = "message-id", defaultValue = "") String messageId,
        @Header(value = "item-id", defaultValue = "") String itemId
    ) {
        if (payload == null && !messageId.isEmpty()) {
            // Tombstone event = retry completed successfully
            log.info("[P4-Redirect] Tombstone received for messageId={}, itemId={}", messageId, itemId);
            retryStore.markCompleted(itemId, messageId);
        } else if (!messageId.isEmpty()) {
            // Regular registration: a new event entered the retry path
            log.debug("[P4-Redirect] Registering retry for messageId={}, itemId={}", messageId, itemId);
            retryStore.addToRetry(itemId, messageId);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private void sendToRetryPath(String message, String messageId, String itemId) {
        // 1. Record in redirect topic (so we can restore state after restart)
        producer.sendToRedirectRegistration(messageId, itemId);
        // 2. Record locally in the in-memory store
        retryStore.addToRetry(itemId, messageId);
        // 3. Send message to retry topic with its ID in the header
        producer.sendToRetry(message, messageId, itemId);
    }

    private void validate(String message) {
        if (!message.startsWith("ORDER:")) {
            throw new IllegalArgumentException("Invalid format: " + message);
        }
    }

    private String extractItemId(String message) {
        String[] parts = message.split(":");
        return parts.length > 1 ? parts[1] : "UNKNOWN";
    }

    private String process(String message, double price) {
        return "PROCESSED[price=" + price + "]: " + message;
    }
}
