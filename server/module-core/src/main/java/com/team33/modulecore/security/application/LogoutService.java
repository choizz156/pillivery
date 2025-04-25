package com.team33.modulecore.security.application;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.team33.modulecore.security.infra.JwtTokenProvider;
import com.team33.modulecore.security.infra.LogoutRedisDao;
import com.team33.modulecore.security.infra.SecretKey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class LogoutService {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecretKey secretKey;
    private final LogoutRedisDao logoutRedisDao;

    public void doLogout(String jws) {
        if (isExpiredToken(jws)) {
            logoutRedisDao.save( jws);
        }
    }

    public boolean isLogoutAlready(HttpServletRequest request) {
        String jws = jwtTokenProvider.extractJws(request);
        return logoutRedisDao.exists(jws);
    }

    private boolean isExpiredToken(String jws) {
        return jwtTokenProvider
            .getJws(jws, secretKey.getSecretKey()).getBody()
            .getExpiration()
            .after(new Date());
    }
}
