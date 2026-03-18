package com.example.resilience_patterns_demo.controller;

import com.example.resilience_patterns_demo.service.ResilientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletionStage;

/*
Função:
GET /api/demo/circuitbreaker → Testa Circuit Breaker
GET /api/demo/retry → Testa Retry
GET /api/demo/ratelimiter → Testa Rate Limiter
GET /api/demo/bulkhead → Testa Bulkhead
GET /api/demo/timelimiter → Testa Time Limiter
*/
@RestController
@RequestMapping("/api/demo")
public class ResilienceController {

    @Autowired
    private ResilientService resilientService;

    @GetMapping("/circuitbreaker")
    public CompletionStage<String> demoCircuitBreaker() {
        return resilientService.circuitBreakerCall();
    }

    @GetMapping("/retry")
    public String demoRetry() throws Exception {
        return resilientService.retryCall();
    }

    @GetMapping("/ratelimiter")
    public String demoRateLimiter() {
        return resilientService.rateLimiterCall();
    }

    @GetMapping("/bulkhead")
    public CompletionStage<String> demoBulkhead() {
        return resilientService.bulkheadCall();
    }

    @GetMapping("/timelimiter")
    public CompletionStage<String> demoTimeLimiter() {
        return resilientService.timeLimiterCall();
    }
}