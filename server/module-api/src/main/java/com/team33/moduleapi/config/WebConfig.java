package com.team33.moduleapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final RedisTemplate<String, Long> restTemplate;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(new ViewInterceptor(restTemplate))
			.addPathPatterns("/items/{itemId}");

	}
}
