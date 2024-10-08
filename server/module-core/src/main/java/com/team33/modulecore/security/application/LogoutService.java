package com.team33.modulecore.security.application;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import com.team33.modulecore.security.infra.JwtTokenProvider;
import com.team33.modulecore.security.infra.SecretKey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class LogoutService {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecretKey secretKey;

    private static final String LOGOUT_PREFIX = "logoutToken ";
    private static final ExpiringMap<String, String> LOGOUT_STORE =
        ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(20, TimeUnit.MINUTES)
            .maxSize(1000)
            .build();

    public void doLogout(String jws) {

        if (isExpiredToken(jws)) {
            LOGOUT_STORE.put(LOGOUT_PREFIX + jws, "token");
            log.info("로그아웃 완료");
        }
    }

    public boolean isLogoutAlready(HttpServletRequest request) {
        String jws = jwtTokenProvider.extractJws(request);
        return LOGOUT_STORE.containsKey(LOGOUT_PREFIX + jws);
    }

    private boolean isExpiredToken(String jws) {
        return jwtTokenProvider
            .getJws(jws, secretKey.getSecretKey()).getBody()
            .getExpiration()
            .after(new Date());
    }
}
