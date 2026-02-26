package com.example.kafka.pattern2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Pattern2Producer {

    private static final Logger log = LoggerFactory.getLogger(Pattern2Producer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.p2-target}") private String targetTopic;
    @Value("${kafka.topics.p2-error}")  private String errorTopic;

    public Pattern2Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToTarget(String message) {
        kafkaTemplate.send(targetTopic, message);
        log.info("[P2] → target: {}", message);
    }

    public void sendToError(String originalMessage, String reason) {
        // Include the reason as a Kafka header so consumers of the DLQ know why it failed
        var msg = MessageBuilder
            .withPayload(originalMessage)
            .setHeader(KafkaHeaders.TOPIC, errorTopic)
            .setHeader("error-reason", reason)
            .build();
        kafkaTemplate.send(msg);
        log.warn("[P2] → DLQ: {} | reason: {}", originalMessage, reason);
    }

    public void sendToSource(String message) {
        kafkaTemplate.send("p2-source-topic", message);
    }
}
