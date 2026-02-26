package com.example.kafka.pattern1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Pattern1Producer {

    private static final Logger log = LoggerFactory.getLogger(Pattern1Producer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.p1-target}")
    private String targetTopic;

    public Pattern1Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToTarget(String message) {
        kafkaTemplate.send(targetTopic, message);
        log.info("[P1] Sent to target topic: {}", message);
    }

    // Utility: send a message to the source topic for testing
    public void sendToSource(String message) {
        kafkaTemplate.send("p1-source-topic", message);
    }
}
