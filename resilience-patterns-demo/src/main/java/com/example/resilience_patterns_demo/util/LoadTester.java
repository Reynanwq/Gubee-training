package com.example.resilience_patterns_demo.util;

import com.example.resilience_patterns_demo.service.ResilientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

/*
Função:
    Simula carga para testar os padrões sob pressão
    testCircuitBreaker(): 20 chamadas com intervalo para ver abertura/fechamento
    testRateLimiter(): 30 chamadas rápidas para ver rate limiting em ação
    testBulkhead(): 10 chamadas simultâneas para ver isolamento
    Usa @Async para executar em paralelo
*/
@Component
public class LoadTester {

    private static final Logger logger = LoggerFactory.getLogger(LoadTester.class);

    @Autowired
    private ResilientService resilientService;

    @Async
    public CompletableFuture<Void> testCircuitBreaker() {
        AtomicInteger successes = new AtomicInteger(0);
        AtomicInteger failures = new AtomicInteger(0);

        for (int i = 0; i < 20; i++) {
            try {
                String result = resilientService.circuitBreakerCall();
                logger.info("Circuit Breaker - Success {}: {}", i, result);
                successes.incrementAndGet();
            } catch (Exception e) {
                logger.error("Circuit Breaker - Failure {}: {}", i, e.getMessage());
                failures.incrementAndGet();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        logger.info("Circuit Breaker - Total: Successes={}, Failures={}",
                successes.get(), failures.get());
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> testRateLimiter() {
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger rejected = new AtomicInteger(0);

        for (int i = 0; i < 30; i++) {
            try {
                String result = resilientService.rateLimiterCall();
                logger.info("Rate Limiter - Request {} processed", i);
                processed.incrementAndGet();
            } catch (Exception e) {
                logger.warn("Rate Limiter - Request {} rejected: {}", i, e.getMessage());
                rejected.incrementAndGet();
            }
        }

        logger.info("Rate Limiter - Total: Processed={}, Rejected={}",
                processed.get(), rejected.get());
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> testBulkhead() {
        AtomicInteger executed = new AtomicInteger(0);
        AtomicInteger blocked = new AtomicInteger(0);

        CompletableFuture<?>[] futures = new CompletableFuture[10];
        for (int i = 0; i < 10; i++) {
            final int index = i;
            futures[i] = CompletableFuture.supplyAsync(() -> {
                try {
                    CompletionStage<String> result = resilientService.bulkheadCall();
                    logger.info("Bulkhead - Request {} executed", index);
                    executed.incrementAndGet();
                    return result;
                } catch (Exception e) {
                    logger.warn("Bulkhead - Request {} blocked: {}", index, e.getMessage());
                    blocked.incrementAndGet();
                    return null;
                }
            });
        }

        CompletableFuture.allOf(futures).join();

        logger.info("Bulkhead - Total: Executed={}, Blocked={}",
                executed.get(), blocked.get());
        return CompletableFuture.completedFuture(null);
    }
}
