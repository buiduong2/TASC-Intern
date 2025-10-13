package com.product_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.cloudinary.Cloudinary;

@Configuration
@EnableAsync
@EnableConfigurationProperties(RedisConfig.class)
@EnableScheduling
public class MiscConfig {

    @Bean
    Cloudinary cloudinary(@Value("${custom.cloudinary.url}") String url) {
        return new Cloudinary(url);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("cache-cleaner-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean(name = "asyncTaskExecutor")
    ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(0);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("bg-task-job-");
        executor.initialize();
        return executor;

    }

}
