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
import org.springframework.security.access.AuthorizationServiceException;

class RefreshRedisTokenDaoTest {

	private RefreshRedisTokenDao refreshRedisTokenDao;
	private RedissonClient redissonClient;
	private RBucket bucket;

	@BeforeEach
	void setUp() {

		redissonClient = mock(RedissonClient.class);
		bucket = mock(RBucket.class);
		refreshRedisTokenDao = new RefreshRedisTokenDao(redissonClient);
	}

	@DisplayName("리프레시 토큰 저장 테스트")
	@Test
	void test1() {
		// given
		String email = "test@example.com";
		String refreshToken = "refresh-token-value";
		when(redissonClient.getBucket("refresh_token:" + email)).thenReturn(bucket);

		// when
		refreshRedisTokenDao.save(email, refreshToken);

		// then
		verify(bucket, times(1)).set(eq(refreshToken), eq(7L), eq(TimeUnit.DAYS));
	}

	@DisplayName("리프레시 토큰 조회할 수 있다.")
	@Test
	void test2() {
		// given
		String email = "test@example.com";
		String refreshToken = "refresh-token-value";
		when(redissonClient.getBucket(anyString())).thenReturn(bucket);
		when(bucket.get()).thenReturn(refreshToken);

		// when
		String result = refreshRedisTokenDao.get(email);

		// then

		assertThat(result).isEqualTo(refreshToken);
	}

	@DisplayName("리프레시 토큰이 존재하지 ")
	@Test
	void test3() {
		// given
		String email = "test@example.com";
		when(redissonClient.getBucket(anyString())).thenReturn(bucket);
		when(bucket.get()).thenReturn(null);

		// when & then
		assertThatThrownBy(() -> refreshRedisTokenDao.get(email))
			.isInstanceOf(AuthorizationServiceException.class);
	}
}