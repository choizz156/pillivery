package com.team33.modulecore.config;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;


public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger("fileLog");

	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		log.error("Exception in method - {}, message - {} ", method.getName(), ex.getMessage());
		for (Object param : params) {
			log.error("Parameter - {}", param);
		}
	}
}
