package com.team33.modulecore.config;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		log.error("Exception in method - {}, message - {} ", method.getName(), ex.getMessage());
		for (Object param : params) {
			log.error("Parameter - {}", param);
		}
	}
}
