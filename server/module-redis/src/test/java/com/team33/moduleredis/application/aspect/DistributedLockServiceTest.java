package com.team33.moduleredis.application.aspect;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;

@ExtendWith(MockitoExtension.class)
class DistributedLockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock  rLock;

    @InjectMocks
    private DistributedLockService distributedLockService;

    @Test
    void tryLock_Success() throws InterruptedException {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        // When
        boolean result = distributedLockService.hasLock("testLock", 3L, 5L);

        // Then
        assertThat(result).isTrue();
    }


    @Test
    void tryLock_FailWhenRedisError() throws Exception {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
            .thenThrow(new RuntimeException("Redis connection error"));

        // When & Then
        assertThatThrownBy(() -> distributedLockService.hasLock("testLock", 3L, 5L))
            .isInstanceOf(RedisException.class);
    }
}