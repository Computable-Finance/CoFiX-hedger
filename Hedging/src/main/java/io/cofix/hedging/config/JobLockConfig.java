package io.cofix.hedging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class JobLockConfig {
    @Bean
    public Lock getLock() {
        return new ReentrantLock();
    }
}
