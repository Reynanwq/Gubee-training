package com.example.resilience_patterns_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/*
Função:
Simula uma API externa imprevisível (como se fosse um serviço de terceiros)
Comportamentos simulados:
30% → Resposta lenta (timeout)
20% → Erro fatal (RuntimeException)
20% → Erro de rede (IOException)
30% → Sucesso
*/
@Component
public class MockApiClient {

    private static final Logger logger = LoggerFactory.getLogger(MockApiClient.class);
    private final Random random = new Random();
    private int callCounter = 0;

    public String callExternalApi() throws Exception {
        callCounter++;

        int chance = random.nextInt(100);

        if (chance < 30) {
            logger.warn("API slow - simulating timeout");
            TimeUnit.SECONDS.sleep(5);
            return "Slow response";
        } else if (chance < 50) {
            logger.error("API error");
            throw new RuntimeException("Simulated error in external API");
        } else if (chance < 70) {
            logger.warn("API unstable - temporary failure");
            throw new IOException("Simulated connection error");
        } else {
            logger.info("API responded successfully");
            TimeUnit.MILLISECONDS.sleep(500);
            return "API Response: Success!";
        }
    }

    public String callApiWithVariableInstability() throws Exception {
        callCounter++;

        if (callCounter % 3 == 0) {
            logger.warn("API returning 503 - Service Unavailable");
            throw new IOException("Service unavailable");
        }

        if (callCounter > 5 && callCounter < 15) {
            logger.warn("API instability period");
            TimeUnit.SECONDS.sleep(2);
        }

        TimeUnit.MILLISECONDS.sleep(300);
        return "API response with variable instability";
    }
}
