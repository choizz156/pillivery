package com.team33.moduleredis.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.team33.moduleredis.application.aspect.DistributedLockService;
import com.team33.moduleredis.domain.annotation.DistributedLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class DistributedLockAspect {

	private final DistributedLockService distributedLockService;

	@Around("@annotation(com.team33.moduleredis.domain.annotation.DistributedLock)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		String lockKey = distributedLock.key();
		long tryLockTimeOutSecond = distributedLock.tryLockTimeOutSecond();
		long lockLeaseTimeOutSecond = distributedLock.lockLeaseTimeOutSecond();

		try {
			checkLock(lockKey, tryLockTimeOutSecond, lockLeaseTimeOutSecond);
			return joinPoint.proceed();
		} finally {
			distributedLockService.releaseLock(lockKey);
		}
	}

	private boolean hasNotLock(String lockKey, long tryLockTimeOutSecond, long lockLeaseTimeOutSecond) {

		return !distributedLockService.hasLock(lockKey, tryLockTimeOutSecond, lockLeaseTimeOutSecond);
	}

	private void checkLock(String lockKey, long tryLockTimeOutSecond, long lockLeaseTimeOutSecond) {
		if (hasNotLock(lockKey, tryLockTimeOutSecond, lockLeaseTimeOutSecond)) {
			throw new IllegalStateException("락 획득 실패: " + lockKey);
		}
	}
}