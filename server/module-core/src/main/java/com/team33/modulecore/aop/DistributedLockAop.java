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

	private static final long TRY_LOCK_TIMEOUT = 3L;
	private static final long LOCK_LEASE_TIME = 5L;
	private final RedissonClient redissonClient;

	@Around("execution(* com.team33.modulecore.core.item.application.ItemStarService.*StarAvg(..))")
	public Object distributedLock(ProceedingJoinPoint joinPoint) {

		checkItemType(joinPoint);

		Item arg = (Item)joinPoint.getArgs()[0];
		String key = "item Id lock :" + arg.getId();
		RLock rLock = redissonClient.getLock(key);

		try {
			boolean hasLock = rLock.tryLock(TRY_LOCK_TIMEOUT, LOCK_LEASE_TIME, TimeUnit.SECONDS);
			if (!hasLock) {
				log.warn("락 획득 실패 : {}", key);
				return false;
			}

			return joinPoint.proceed();
		} catch (Throwable e) {
			throw new RuntimeException("분산 락 관련 에러", e);
		} finally {
			unlock(rLock);
		}
	}

	private static void checkItemType(ProceedingJoinPoint joinPoint) {
		if (!(joinPoint.getArgs()[0] instanceof Item)) {
			throw new IllegalArgumentException("Item 타입이 아닙니다.");
		}
	}

	private void unlock(RLock rLock) {
		if (rLock.isHeldByCurrentThread())
			rLock.unlock();
	}
}
