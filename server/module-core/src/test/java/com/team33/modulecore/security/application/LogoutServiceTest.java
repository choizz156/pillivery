package com.team33.modulecore.security.application;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.security.domain.RefreshTokenRepository;
import com.team33.modulecore.security.domain.SecretKey;
import com.team33.modulecore.security.infra.JwtTokenProvider;
import com.team33.modulecore.security.infra.LogoutRedisDao;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

class LogoutServiceTest {

    private LogoutService logoutService;
    private JwtTokenProvider jwtTokenProvider;
    private SecretKey secretKey;
    private LogoutRedisDao logoutRedisDao;
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = mock(JwtTokenProvider.class);
        secretKey = mock(SecretKey.class);
        logoutRedisDao = mock(LogoutRedisDao.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        logoutService = new LogoutService(jwtTokenProvider, secretKey, logoutRedisDao, refreshTokenRepository);
    }

    @Test
    @DisplayName("만료되지 않은 토큰으로 로그아웃 시 리프레시 토큰이 삭제되어야 한다")
    void test1() {
        // given
        String jws = "test_token";
        String username = "test@email.com";

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        Jws<Claims> mockJws = mock(Jws.class);
        Claims mockClaims = mock(Claims.class);

        given(secretKey.getSecretKey()).willReturn(null);
        given(jwtTokenProvider.getJws(anyString(), any())).willReturn(mockJws);
        given(mockJws.getBody()).willReturn(mockClaims);
        given(mockClaims.getExpiration()).willReturn(new Date(System.currentTimeMillis() + 1000000));
        given(mockClaims.get("username")).willReturn(username);
        given(jwtTokenProvider.getJwsBody(anyString())).willReturn(claims);

        // when
        logoutService.doLogout(jws);

        // then
        verify(logoutRedisDao, times(1)).save(jws);
        verify(refreshTokenRepository, times(1)).delete(username);
    }
}