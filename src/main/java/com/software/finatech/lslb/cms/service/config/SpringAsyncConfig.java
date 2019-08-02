package com.software.finatech.lslb.cms.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class SpringAsyncConfig {
    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(400);
        threadPoolTaskExecutor.setCorePoolSize(200);
        threadPoolTaskExecutor.setQueueCapacity(5000);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
