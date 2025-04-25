package com.team33.moduleredis.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class RedisTokenTest {

    private RedisServer redisServer;
    private RedissonClient redissonClient;
    private RefreshTokenRedisRepository refreshTokenRepository;
    private LogoutRedisRepository logoutRepository;

    @BeforeEach
    void setUp() throws IOException {
        // 임베디드 Redis 서버 시작
        redisServer = new RedisServer(6379);
        redisServer.start();
        log.info("Embedded Redis started on port: 6379");

        // Redisson 클라이언트 설정
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://127.0.0.1:6379")
            .setConnectionMinimumIdleSize(1)
            .setConnectionPoolSize(1);

        redissonClient = Redisson.create(config);
        refreshTokenRepository = new RefreshTokenRedisRepository(redissonClient);
        logoutRepository = new LogoutRedisRepository(redissonClient);
    }

    @AfterEach
    void tearDown() {
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
        if (redisServer != null) {
            try {
                redisServer.stop();
            } catch (IOException e) {
                log.error("Redis 서버 종료 중 오류 발생", e);
            }
        }
    }

    @Test
    @DisplayName("RefreshToken을 저장하고 조회할 수 있다")
    void saveAndGetRefreshToken() {
        // given
        String email = "test@example.com";
        String refreshToken = "test-refresh-token";

        // when
        refreshTokenRepository.save(email, refreshToken);
        Optional<String> foundToken = refreshTokenRepository.get(email);

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get()).isEqualTo(refreshToken);
        log.info("RefreshToken 저장 및 조회 테스트 성공");
    }

    @Test
    @DisplayName("RefreshToken이 만료되면 조회할 수 없다")
    void refreshTokenExpires() throws InterruptedException {
        // given
        String email = "test@example.com";
        String refreshToken = "test-refresh-token";
        refreshTokenRepository.save(email, refreshToken);

        // when
        Thread.sleep(1000); // 1초 대기
        Optional<String> foundToken = refreshTokenRepository.get(email);

        // then
        assertThat(foundToken).isEmpty();
        log.info("RefreshToken 만료 테스트 성공");
    }

    @Test
    @DisplayName("로그아웃 토큰을 저장하고 확인할 수 있다")
    void saveAndCheckLogoutToken() {
        // given
        String token = "test-logout-token";

        // when
        logoutRepository.save(token);
        boolean exists = logoutRepository.exists(token);

        // then
        assertThat(exists).isTrue();
        log.info("로그아웃 토큰 저장 및 확인 테스트 성공");
    }

    @Test
    @DisplayName("로그아웃 토큰이 만료되면 존재하지 않는다")
    void logoutTokenExpires() throws InterruptedException {
        // given
        String token = "test-logout-token";
        logoutRepository.save(token);

        // when
        Thread.sleep(1000); // 1초 대기
        boolean exists = logoutRepository.exists(token);

        // then
        assertThat(exists).isFalse();
        log.info("로그아웃 토큰 만료 테스트 성공");
    }
} 