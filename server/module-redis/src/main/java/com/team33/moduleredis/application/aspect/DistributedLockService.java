package com.team33.moduleredis.application.aspect;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockService {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	private static final String LOCK_PREFIX = "lock:";

	private final RedissonClient redissonClient;

	public boolean tryLock(String lockKey, long tryLockTime, long leaseTime) {

		String key = LOCK_PREFIX + lockKey;
		RLock lock = redissonClient.getLock(key);

		try {
			return lock.tryLock(tryLockTime, leaseTime, TimeUnit.SECONDS);
		} catch (Exception e) {
			LOGGER.warn("Failed to lock lock", e);
			throw new RedisException("분산 락 관련 에러", e);
		}
	}

	public void releaseLock(String lockKey) {

		String key = LOCK_PREFIX + lockKey;
		RLock lock = redissonClient.getLock(key);

		if(lock.isHeldByCurrentThread()) {
			lock.unlock();
		}
	}
}