package com.example.patterns.consumer;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SimpleConsumers {

    // PADRÃO 1: Stop on Error
    @KafkaListener(topics = "pattern1-stop", groupId = "group-1")
    public void consumeStop(String message) {
        log.info("P1: Recebido: {}", message);
        if (message.contains("ERRO")) {
            throw new RuntimeException("P1: Erro fatal - Travando consumidor.");
        }
    }

    // PADRÃO 2: Dead Letter Queue (DLQ)
    @RetryableTopic(
            attempts = "1",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "pattern2-dlq", groupId = "group-2")
    public void consumeDlq(String message) {
        if (message.contains("ERRO")) {
            throw new RuntimeException("P2: Erro - Enviando para DLQ.");
        }
        log.info("P2: Processado: {}", message);
    }

    @DltHandler
    public void handleDlt(String message) {
        log.error("P2: [DLQ] Mensagem recebida para análise: {}", message);
    }

    // PADRÃO 3: Retry Topic
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "pattern3-retry", groupId = "group-3")
    public void consumeRetry(String message) {
        log.info("P3: Tentativa de processamento: {}", message);
        if (message.contains("RETRY")) {
            throw new RuntimeException("P3: Falha temporária - Agendando retry.");
        }
    }
}