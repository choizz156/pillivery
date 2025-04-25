package com.team33.modulecore.security.infra;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class LogoutRedisDao {

	private static final long EXPIRE_TIME = 7; // 7일
	private static final String KEY_PREFIX = "logout_token:";
	private final RedissonClient redissonClient;

	public void save(String token) {

		RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + token);
		bucket.set("logout", EXPIRE_TIME, TimeUnit.DAYS);
	}

	public boolean exists(String token) {

		RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + token);
		return bucket.isExists();
	}
}
