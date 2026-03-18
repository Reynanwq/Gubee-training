package com.example.resilience_patterns_demo.config;

/*
Configura o pool de threads para execução assíncrona
CorePoolSize (10): threads sempre disponíveis
MaxPoolSize (20): máximo de threads quando há pico
QueueCapacity (100): fila de espera quando todas threads estão ocupadas
Permite que os padrões Bulkhead e TimeLimiter funcionem corretamente

*/


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Resilience-");
        executor.initialize();
        return executor;
    }
}
