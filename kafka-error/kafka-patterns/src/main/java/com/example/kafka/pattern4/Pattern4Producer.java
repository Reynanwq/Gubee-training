package com.example.kafka.pattern4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Pattern4Producer {

    private static final Logger log = LoggerFactory.getLogger(Pattern4Producer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.p4-target}")   private String targetTopic;
    @Value("${kafka.topics.p4-error}")    private String errorTopic;
    @Value("${kafka.topics.p4-retry}")    private String retryTopic;
    @Value("${kafka.topics.p4-redirect}") private String redirectTopic;

    public Pattern4Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToTarget(String message) {
        kafkaTemplate.send(targetTopic, message);
        log.info("[P4] → target: {}", message);
    }

    public void sendToError(String message, String reason) {
        var msg = MessageBuilder.withPayload(message)
            .setHeader(KafkaHeaders.TOPIC, errorTopic)
            .setHeader("error-reason", reason)
            .build();
        kafkaTemplate.send(msg);
        log.warn("[P4] → error: {} | reason: {}", message, reason);
    }

    public void sendToRetry(String message, String messageId, String itemId) {
        sendToRetry(message, messageId, itemId, 0);
    }

    public void sendToRetry(String message, String messageId, String itemId, int retryCount) {
        var msg = MessageBuilder.withPayload(message)
            .setHeader(KafkaHeaders.TOPIC, retryTopic)
            .setHeader("message-id", messageId)
            .setHeader("item-id", itemId)
            .setHeader("retry-count", retryCount)
            .build();
        kafkaTemplate.send(msg);
        log.info("[P4] → retry (count={}), messageId={}, itemId={}", retryCount, messageId, itemId);
    }

    /**
     * Publishes a registration event to the redirect topic.
     * Used to persist the retry state so it can be restored after a restart.
     */
    public void sendToRedirectRegistration(String messageId, String itemId) {
        var msg = MessageBuilder.withPayload("RETRY_REGISTERED")
            .setHeader(KafkaHeaders.TOPIC, redirectTopic)
            .setHeader("message-id", messageId)
            .setHeader("item-id", itemId)
            .build();
        kafkaTemplate.send(msg);
        log.debug("[P4] → redirect (register): messageId={}, itemId={}", messageId, itemId);
    }

    /**
     * Tombstone event: null payload signals that a retry completed successfully.
     * The main consumer uses this to remove the message from the OrderedRetryStore.
     */
    public void sendTombstone(String messageId, String itemId) {
        var msg = MessageBuilder.<String>withPayload(null)
            .setHeader(KafkaHeaders.TOPIC, redirectTopic)
            .setHeader("message-id", messageId)
            .setHeader("item-id", itemId)
            .build();
        kafkaTemplate.send(msg);
        log.info("[P4] → redirect (tombstone): messageId={}, itemId={}", messageId, itemId);
    }

    public void sendToSource(String message) {
        kafkaTemplate.send("p4-source-topic", message);
    }
}
