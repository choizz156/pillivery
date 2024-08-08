package com.team33.modulecore.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.item.domain.entity.Item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class DistributedLockAop {

	private final RedisTemplate<String, Object> redisTemplate;

	@Around("execution(* com.team33.modulecore.core.item.application.ItemStarService.*StarAvg(..))")
	public Object distributedLock(ProceedingJoinPoint joinPoint) {
		Item arg = (Item)joinPoint.getArgs()[0];
		String key = "item Id lock :" + arg.getId();
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();

		Boolean hasLock = false;
		long start = System.currentTimeMillis();
		long retrySecond = 3000L;

		while (!hasLock && System.currentTimeMillis() - start < retrySecond) {
			hasLock = ops.setIfAbsent(key, "true", 10L, TimeUnit.SECONDS);

			if (!hasLock) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (!hasLock) {
			throw new RuntimeException("락을 획득할 수 없습니다.");
		}

		try {
			return joinPoint.proceed();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			redisTemplate.delete(key);
		}
	}
}
