package com.example.patterns.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RedirectOrderConsumer {

    private final Map<String, String> activeRetries = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public RedirectOrderConsumer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "pattern4-source", groupId = "group-4")
    public void mainConsumer(String message) {
        String[] parts = message.split(":");
        String itemId = parts[0];
        String content = parts[1];
        String uid = parts[2];

        if (activeRetries.containsKey(itemId)) {
            log.warn("P4: Chave {} bloqueada. Redirecionando {} para manter ordem.", itemId, uid);
            kafkaTemplate.send("pattern4-retry", message);
            return;
        }

        if (content.equals("FALHA")) {
            log.error("P4: Falha no item {}. Bloqueando e movendo UID: {}", itemId, uid);
            activeRetries.put(itemId, uid);
            kafkaTemplate.send("pattern4-retry", message);
            kafkaTemplate.send("pattern4-redirect", "UID:" + uid + ":ITEM:" + itemId);
        } else {
            log.info("P4: Sucesso no processamento: {}", content);
        }
    }

    @KafkaListener(topics = "pattern4-redirect", groupId = "group-4-monitor")
    public void monitorRedirect(String signal) {
        if (signal.startsWith("TS:")) { // Tombstone Signal
            String uid = signal.split(":")[1];
            activeRetries.values().remove(uid);
            log.info("P4: [Tombstone] UID {} processado. Chave liberada.", uid);
        }
    }
}