package com.team33.moduleapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.team33.moduleredis.interceptor.ViewInterceptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final ViewInterceptor viewInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(viewInterceptor)
			.addPathPatterns("/api/items/{itemId}");
	}
}
