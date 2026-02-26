package com.example.patterns.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topic1() { return TopicBuilder.name("pattern1-stop").partitions(1).build(); }

    @Bean
    public NewTopic topic2() { return TopicBuilder.name("pattern2-dlq").partitions(1).build(); }

    @Bean
    public NewTopic topic3() { return TopicBuilder.name("pattern3-retry").partitions(1).build(); }

    @Bean
    public NewTopic topic4Source() { return TopicBuilder.name("pattern4-source").partitions(1).build(); }

    @Bean
    public NewTopic topic4Retry() { return TopicBuilder.name("pattern4-retry").partitions(1).build(); }

    @Bean
    public NewTopic topic4Redirect() { return TopicBuilder.name("pattern4-redirect").partitions(1).build(); }
}