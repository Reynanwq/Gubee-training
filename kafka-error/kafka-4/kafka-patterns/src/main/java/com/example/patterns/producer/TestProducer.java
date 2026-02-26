package com.example.patterns.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendTestMessages() {
        log.info("Enviando mensagens de teste...");

        // Teste Padrão 1 (Stop on Error)
        kafkaTemplate.send("pattern1-stop", "MENSAGEM_ERRO");

        // Teste Padrão 2 (DLQ)
        kafkaTemplate.send("pattern2-dlq", "MENSAGEM_ERRO");

        // Teste Padrão 3 (Retry)
        kafkaTemplate.send("pattern3-retry", "MENSAGEM_RETRY");

        // Teste Padrão 4 (Order)
        kafkaTemplate.send("pattern4-source", "A:FALHA:UID_ALFA");
        kafkaTemplate.send("pattern4-source", "A:SUCESSO:UID_BETA");
    }
}