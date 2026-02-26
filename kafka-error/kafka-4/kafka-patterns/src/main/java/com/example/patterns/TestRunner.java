package com.example.patterns;

import com.example.patterns.producer.TestProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {

    private final TestProducer testProducer;

    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(3000);
        testProducer.sendTestMessages();
    }
}