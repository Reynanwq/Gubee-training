package com.example.resilience_patterns_demo.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
public class ResilientService {

    private static final Logger logger = LoggerFactory.getLogger(ResilientService.class);

    @Autowired
    private MockApiClient mockApiClient;

    @CircuitBreaker(name = "apiService", fallbackMethod = "circuitBreakerFallback")
    @TimeLimiter(name = "apiService", fallbackMethod = "circuitBreakerFallback")
    public CompletionStage<String> circuitBreakerCall() {
        CompletableFuture<String> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                String result = mockApiClient.callExternalApi();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    // RETRY
    @Retry(name = "apiService", fallbackMethod = "retryFallback")
    public String retryCall() throws Exception {
        logger.info("Retry call - Attempting operation that might fail");
        return mockApiClient.callApiWithVariableInstability();
    }

    // RATE LIMITER
    @RateLimiter(name = "apiService", fallbackMethod = "rateLimiterFallback")
    public String rateLimiterCall() {
        logger.info("Rate Limiter call - Checking request limit");
        return "Request processed within limit";
    }

    // BULKHEAD
    @Bulkhead(name = "apiService",
            type = Bulkhead.Type.THREADPOOL,
            fallbackMethod = "bulkheadFallback")
    public CompletionStage<String> bulkheadCall() {
        logger.info("Bulkhead call - Isolating system resources");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return mockApiClient.callExternalApi();
            } catch (Exception e) {
                logger.error("Error in bulkhead call: {}", e.getMessage());
                throw new RuntimeException("Error in bulkhead call", e);
            }
        });
    }

    // TIME LIMITER
    @TimeLimiter(name = "apiService", fallbackMethod = "timeLimiterFallback")
    public CompletionStage<String> timeLimiterCall() {
        logger.info("Time Limiter call - Waiting for response within timeout");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return mockApiClient.callExternalApi();
            } catch (Exception e) {
                logger.error("Error in time limiter call: {}", e.getMessage());
                throw new RuntimeException("Error in time limiter call", e);
            }
        });
    }

    private CompletionStage<String> circuitBreakerFallback(Exception e) {
        logger.warn("Circuit Breaker/TimeLimiter fallback: {}", e.getMessage());
        return CompletableFuture.completedFuture(
                "Circuit Breaker activated - Service temporarily unavailable"
        );
    }

    private String retryFallback(Exception e) {
        logger.warn("Retry fallback after all attempts: {}", e.getMessage());
        return "Retry exhausted attempts - Service unstable";
    }

    private String rateLimiterFallback(Exception e) {
        logger.warn("Rate Limiter fallback: {}", e.getMessage());
        return "Too many requests - Rate limit exceeded. Try again in a few seconds";
    }

    private CompletionStage<String> bulkheadFallback(Exception e) {
        logger.warn("Bulkhead fallback: {}", e.getMessage());
        return CompletableFuture.completedFuture(
                "Bulkhead activated - Resource isolated due to overload"
        );
    }

    private CompletionStage<String> timeLimiterFallback(Exception e) {
        logger.warn("Time Limiter fallback: {}", e.getMessage());
        return CompletableFuture.completedFuture(
                "Time Limiter activated - Timeout exceeded"
        );
    }
}