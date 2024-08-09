package com.team33.modulecore.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.item.domain.entity.Item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class DistributedLockAop {

	private final RedissonClient redissonClient;

	@Around("execution(* com.team33.modulecore.core.item.application.ItemStarService.*StarAvg(..))")
	public Object distributedLock(ProceedingJoinPoint joinPoint) {
		Item arg = (Item)joinPoint.getArgs()[0];
		String key = "item Id lock :" + arg.getId();

		RLock rLock = redissonClient.getLock(key);

		try {
			boolean hasLock = rLock.tryLock(3L, 5L, TimeUnit.SECONDS);

			if (!hasLock) {
				log.warn("락 획득 실패 : {}", key);
				return false;
			}

			return joinPoint.proceed();
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			unlock(rLock, key);
		}
	}

	private void unlock(RLock rLock, String key) {
		try {
			rLock.unlock();
		} catch (IllegalMonitorStateException e) {
			log.warn("lock failed : {}, key : {}", e.getMessage(), key);
		}
	}
}
