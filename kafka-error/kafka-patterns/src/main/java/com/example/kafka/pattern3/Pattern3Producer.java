package com.example.kafka.pattern3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Pattern3Producer {

    private static final Logger log = LoggerFactory.getLogger(Pattern3Producer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.p3-target}") private String targetTopic;
    @Value("${kafka.topics.p3-error}")  private String errorTopic;
    @Value("${kafka.topics.p3-retry}")  private String retryTopic;

    public Pattern3Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToTarget(String message) {
        kafkaTemplate.send(targetTopic, message);
        log.info("[P3] → target: {}", message);
    }

    public void sendToError(String message, String reason) {
        var msg = MessageBuilder.withPayload(message)
            .setHeader(KafkaHeaders.TOPIC, errorTopic)
            .setHeader("error-reason", reason)
            .build();
        kafkaTemplate.send(msg);
        log.warn("[P3] → error: {} | reason: {}", message, reason);
    }

    public void sendToRetry(String message) {
        sendToRetry(message, 0);
    }

    public void sendToRetry(String message, int currentRetryCount) {
        var msg = MessageBuilder.withPayload(message)
            .setHeader(KafkaHeaders.TOPIC, retryTopic)
            .setHeader("retry-count", currentRetryCount + 1)
            .build();
        kafkaTemplate.send(msg);
        log.info("[P3] → retry (count={}): {}", currentRetryCount + 1, message);
    }

    public void sendToSource(String message) {
        kafkaTemplate.send("p3-source-topic", message);
    }
}
