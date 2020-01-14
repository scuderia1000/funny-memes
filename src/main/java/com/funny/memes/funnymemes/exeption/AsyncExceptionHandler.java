package com.funny.memes.funnymemes.exeption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        LOG.error("Exception in method: {}", method.getName());
        LOG.error("Exception message: {}", throwable.getMessage());
        for (Object param : objects) {
            LOG.error("Param value: {}", param);
        }
    }
}
