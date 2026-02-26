package com.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    // Pattern 1
    @Bean public NewTopic p1Source()  { return TopicBuilder.name("p1-source-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p1Target()  { return TopicBuilder.name("p1-target-topic").partitions(1).replicas(1).build(); }

    // Pattern 2
    @Bean public NewTopic p2Source()  { return TopicBuilder.name("p2-source-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p2Target()  { return TopicBuilder.name("p2-target-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p2Error()   { return TopicBuilder.name("p2-error-topic").partitions(1).replicas(1).build(); }

    // Pattern 3
    @Bean public NewTopic p3Source()  { return TopicBuilder.name("p3-source-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p3Target()  { return TopicBuilder.name("p3-target-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p3Error()   { return TopicBuilder.name("p3-error-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p3Retry()   { return TopicBuilder.name("p3-retry-topic").partitions(1).replicas(1).build(); }

    // Pattern 4
    @Bean public NewTopic p4Source()   { return TopicBuilder.name("p4-source-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p4Target()   { return TopicBuilder.name("p4-target-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p4Error()    { return TopicBuilder.name("p4-error-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p4Retry()    { return TopicBuilder.name("p4-retry-topic").partitions(1).replicas(1).build(); }
    @Bean public NewTopic p4Redirect() { return TopicBuilder.name("p4-redirect-topic").partitions(1).replicas(1).build(); }
}
