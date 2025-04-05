package com.team33.moduleredis.interceptor;

import java.time.Duration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.redisson.api.RHyperLogLog;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ViewInterceptor implements HandlerInterceptor {

	private final RedissonClient redissonClient;

	@Override
	public void postHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler,
		ModelAndView modelAndView
	) throws Exception {

		String pathInfo = request.getRequestURI();
		String remoteAddr = request.getRemoteAddr();

		int itemId = Character.getNumericValue(pathInfo.charAt(pathInfo.length() - 1));

		RSet<Integer> viewedItem = redissonClient.getSet("viewCount");
		viewedItem.add(itemId);

		RHyperLogLog<String> viewCheck = redissonClient.getHyperLogLog(String.valueOf(itemId));
		viewCheck.expire(Duration.ofDays(1L));
		viewCheck.add(remoteAddr);
	}
}
