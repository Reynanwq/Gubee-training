package com.example.patterns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class KafkaPatternsApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaPatternsApplication.class, args);
    }
}