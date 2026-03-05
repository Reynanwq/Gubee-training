package com.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class EventApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventApplication.class, args);
	}

}
