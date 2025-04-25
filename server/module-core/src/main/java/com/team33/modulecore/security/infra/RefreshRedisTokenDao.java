package com.team33.modulecore.security.infra;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.security.domain.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RefreshRedisTokenDao implements RefreshTokenRepository {

    private static final long EXPIRE_TIME = 7;
    private static final String KEY_PREFIX = "refresh_token:";

    private final RedissonClient redissonClient;

    public void save(String email, String refreshToken) {
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + email);
        bucket.set(refreshToken, EXPIRE_TIME, TimeUnit.DAYS);
    }

    public String get(String email) {
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + email);

        return Optional.ofNullable(bucket.get())
            .orElseThrow(() -> new AuthorizationServiceException(email + "조회된 리프레시 토큰 없음."));
    }
}
