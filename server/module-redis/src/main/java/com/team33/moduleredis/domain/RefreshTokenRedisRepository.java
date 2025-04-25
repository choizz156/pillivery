package com.team33.moduleredis.domain;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final RedissonClient redissonClient;
    private static final String KEY_PREFIX = "refresh_token:";
    private static final long EXPIRE_TIME = 7; // 7일

    public void save(String email, String refreshToken) {
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + email);
        bucket.set(refreshToken, EXPIRE_TIME, TimeUnit.DAYS);
    }

    public Optional<String> get(String email) {
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + email);
        return Optional.ofNullable(bucket.get());
    }

    public void delete(String email) {
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + email);
        bucket.delete();
    }
} 