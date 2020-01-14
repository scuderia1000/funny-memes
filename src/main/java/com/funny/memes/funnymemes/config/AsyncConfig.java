package com.funny.memes.funnymemes.config;

import com.funny.memes.funnymemes.exeption.AsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Value("${thread-task-executor.core.pool.size}")
    private Integer propertyCorePoolSize;

    @Value("${thread-task-executor.max.pool.size}")
    private int propertyMaxPoolSize;

    @Value("${thread-task-executor.queue.capacity}")
    private int propertyQueueCapacity;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(propertyCorePoolSize);
        executor.setMaxPoolSize(propertyMaxPoolSize);
        executor.setQueueCapacity(propertyQueueCapacity);
        executor.setThreadNamePrefix("MemeParserAsyncThread-");
        executor.initialize();

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}
