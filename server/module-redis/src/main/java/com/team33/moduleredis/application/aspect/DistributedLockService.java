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
@RequiredArgsConstructor
@Service
public class DistributedLockService {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	private static final String LOCK_PREFIX = "lock:";

	private final RedissonClient redissonClient;

	public boolean hasLock(String lockKey, long tryLockTime, long leaseTime) {

		String key = LOCK_PREFIX + lockKey;
		RLock lock = redissonClient.getLock(key);

		try {
			return lock.tryLock(tryLockTime, leaseTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOGGER.warn("redis error", e);
			Thread.currentThread().interrupt();
			return false;
		} catch (Exception e) {
			LOGGER.error("Redisson 락 획득 중 오류", e);
			throw new RedisException("Redisson 락 획득 실패", e);
		}
	}

	public void releaseLock(String lockKey) {

		String key = LOCK_PREFIX + lockKey;
		RLock lock = redissonClient.getLock(key);

		try {
			unlock(lock);
		} catch (Exception e) {
			LOGGER.warn("redis error", e);
		}
	}

	private void unlock(RLock lock) {

		if (lock.isHeldByCurrentThread()) {
			lock.unlock();
		}
	}

}