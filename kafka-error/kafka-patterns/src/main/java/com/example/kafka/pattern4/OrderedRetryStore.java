package com.example.kafka.pattern4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store that tracks which items currently have events
 * in the retry path.
 *
 * Key: itemId
 * Value: ordered list of message IDs (UUIDs) that are being retried
 *
 * Recovery: if the application restarts, rebuild this store by
 * reading the redirect topic from the beginning.
 */
@Component
public class OrderedRetryStore {

    private static final Logger log = LoggerFactory.getLogger(OrderedRetryStore.class);

    // itemId -> ordered list of messageIds currently in retry
    private final Map<String, LinkedList<String>> retryingItems = new ConcurrentHashMap<>();

    public void addToRetry(String itemId, String messageId) {
        retryingItems.computeIfAbsent(itemId, k -> new LinkedList<>()).add(messageId);
        log.debug("[Store] Added messageId={} for itemId={}. Queue: {}", messageId, itemId, retryingItems.get(itemId));
    }

    public boolean isRetrying(String itemId) {
        LinkedList<String> queue = retryingItems.get(itemId);
        return queue != null && !queue.isEmpty();
    }

    public void markCompleted(String itemId, String messageId) {
        LinkedList<String> queue = retryingItems.get(itemId);
        if (queue != null) {
            queue.remove(messageId);
            if (queue.isEmpty()) {
                retryingItems.remove(itemId);
                log.debug("[Store] All retries complete for itemId={}", itemId);
            } else {
                log.debug("[Store] Removed messageId={} for itemId={}. Remaining: {}", messageId, itemId, queue);
            }
        }
    }

    public Map<String, LinkedList<String>> getSnapshot() {
        return Collections.unmodifiableMap(retryingItems);
    }
}
