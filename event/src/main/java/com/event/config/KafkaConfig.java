package com.event.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.notification}")
    private String notificationTopic;

    @Value("${kafka.topics.carried-data}")
    private String carriedDataTopic;

    @Value("${kafka.topics.event-sourcing}")
    private String eventSourcingTopic;

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name(notificationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic carriedDataTopic() {
        return TopicBuilder.name(carriedDataTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic eventSourcingTopic() {
        // Log compaction: mantém o último evento por chave (ideal para Event Sourcing)
        return TopicBuilder.name(eventSourcingTopic)
                .partitions(1)
                .replicas(1)
                .config("cleanup.policy", "delete") // trocar para "compact" em produção
                .build();
    }
}
