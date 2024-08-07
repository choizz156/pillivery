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

	private static final long VIEW_CHECK_TIME = 60L * 60L * 24L;

	private final RedisTemplate<String, Long> restTemplate;

	public void postHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler,
		ModelAndView modelAndView
	) throws Exception {

		String pathInfo = request.getRequestURI();
		String remoteAddr = request.getRemoteAddr();

		int key = Character.getNumericValue(pathInfo.charAt(pathInfo.length() - 1));
		String viewCheckKey = key + " : " + remoteAddr;

		HashOperations<String, String, Long> hashOps = restTemplate.opsForHash();
		Long lastView = hashOps.get("view_check", viewCheckKey);

		long now = System.currentTimeMillis();
		if (lastView == null || now - lastView > VIEW_CHECK_TIME) {
			hashOps.put("view_check", viewCheckKey, now);
			hashOps.increment("view_count", String.valueOf(key), 1L);
		}
	}
}
