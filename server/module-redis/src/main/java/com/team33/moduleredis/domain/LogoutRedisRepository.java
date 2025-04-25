package com.team33.moduleredis.domain;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class LogoutRedisRepository {

    private final RedissonClient redissonClient;
    private static final String KEY_PREFIX = "logout_token:";
    private static final long EXPIRE_TIME = 7; // 7일

    public void save(String token) {
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + token);
        bucket.set("logout", EXPIRE_TIME, TimeUnit.DAYS);
    }

    public boolean exists(String token) {
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + token);
        return bucket.isExists();
    }
} 