package com.team33.modulecore.security.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

class LogoutRedisDaoTest {

    private RedissonClient redissonClient;

    private RBucket bucket;

    private LogoutRedisDao logoutRedisDao;


    @BeforeEach
    void setUp() {

        redissonClient = mock(RedissonClient.class);
        bucket = mock(RBucket.class);
        logoutRedisDao = new LogoutRedisDao(redissonClient);
    }

    @Test
    @DisplayName("토큰 저장 테스트")
    void test1() {
        // given
        String token = "test-token";
        when(redissonClient.getBucket(anyString())).thenReturn(bucket);

        // when
        logoutRedisDao.save(token);

        // then
        verify(redissonClient).getBucket("logout_token:" + token);
        verify(bucket).set(eq("logout"), eq(7L), eq(TimeUnit.DAYS));
    }

    @Test
    @DisplayName("토큰 존재 여부 확인 테스트 - 존재하는 경우")
    void test2() {
        // given
        String token = "test-token";
        when(redissonClient.getBucket(anyString())).thenReturn(bucket);
        when(bucket.isExists()).thenReturn(true);

        // when
        boolean exists = logoutRedisDao.exists(token);

        // then
        assertThat(exists).isTrue();
        verify(redissonClient).getBucket("logout_token:" + token);
    }

    @Test
    @DisplayName("토큰 존재 여부 확인 테스트 - 존재하지 않는 경우")
    void test3() {
        // given
        String token = "test-token";
        when(redissonClient.getBucket(anyString())).thenReturn(bucket);
        when(bucket.isExists()).thenReturn(false);

        // when
        boolean exists = logoutRedisDao.exists(token);

        // then
        assertThat(exists).isFalse();
        verify(redissonClient).getBucket("logout_token:" + token);
    }
}