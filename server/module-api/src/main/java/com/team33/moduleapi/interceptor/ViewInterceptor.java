package com.team33.moduleapi.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(ViewInterceptor.class);
	private final RedisTemplate<String, Long> restTemplate;

	@Override
	public void postHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler,
		ModelAndView modelAndView
	) throws Exception {
		String pathInfo = request.getRequestURI();
		int key = Character.getNumericValue(pathInfo.charAt(pathInfo.length() - 1));

		HashOperations<String, String, Long> hashOps = restTemplate.opsForHash();
		hashOps.increment("view_count", String.valueOf(key), 1L);
	}
}
