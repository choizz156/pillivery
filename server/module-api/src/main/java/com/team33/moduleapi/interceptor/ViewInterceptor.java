package com.team33.moduleapi.interceptor;

import static com.team33.modulecore.cache.RedisCacheKey.*;

import java.time.Duration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.redisson.api.RHyperLogLog;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewInterceptor implements HandlerInterceptor {

	private final RedissonClient redissonClient;

	public void postHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler,
		ModelAndView modelAndView
	) throws Exception {

		String pathInfo = request.getRequestURI();
		String remoteAddr = request.getRemoteAddr();

		int itemId = Character.getNumericValue(pathInfo.charAt(pathInfo.length() - 1));

		RSet<Integer> viewedItem = redissonClient.getSet(VIEW_COUNT.name());
		viewedItem.add(itemId);

		RHyperLogLog<String> viewCheck = redissonClient.getHyperLogLog(String.valueOf(itemId));
		viewCheck.expire(Duration.ofDays(1L));
		viewCheck.add(remoteAddr);
	}
}
