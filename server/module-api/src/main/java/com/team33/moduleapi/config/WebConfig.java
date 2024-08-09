package com.team33.moduleapi.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.team33.moduleapi.interceptor.ViewInterceptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final RedissonClient redissonClient;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(new ViewInterceptor(redissonClient))
			.addPathPatterns("/items/{itemId}");
	}
}
