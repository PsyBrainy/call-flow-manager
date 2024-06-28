package com.psybrainy.CallFlowManager.share.config;

import com.psybrainy.CallFlowManager.share.properties.ThreadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private final ThreadProperties threadProperties;

    @Autowired
    public AsyncConfig(ThreadProperties threadProperties) {
        this.threadProperties = threadProperties;
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadProperties.getCoreSize());
        executor.setMaxPoolSize(threadProperties.getMaxSize());
        executor.setQueueCapacity(threadProperties.getCapacity());
        executor.setThreadNamePrefix(threadProperties.getNamePrefix());
        executor.initialize();
        return executor;
    }
}
