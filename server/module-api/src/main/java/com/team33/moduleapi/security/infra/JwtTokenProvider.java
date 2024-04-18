package com.team33.moduleapi.security.infra;


import com.team33.modulecore.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    private final SecretKey secretKey;

    public String createAccessToken(
        Map<String, Object> claims,
        String subject,
        Date expiration,
        Key key
    ) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setExpiration(expiration)
            .setIssuedAt(Calendar.getInstance().getTime())
            .signWith(key)
            .compact();
    }

    public String createRefreshToken(
        String subject,
        Date expiration,
        Key key
    ) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(expiration)
            .setIssuedAt(Calendar.getInstance().getTime())
            .signWith(key)
            .compact();
    }

    public String extractJws(HttpServletRequest request) {
        return request.getHeader("Authorization").replace("Bearer ", "");
    }

    public Map<String, Object> getJwsBody(String jws) {
        Key key = secretKey.getSecretKey();
        return getJws(jws, key).getBody();
    }

    public Jws<Claims> getJws(String jws, Key key) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jws);
    }


    public String delegateAccessToken(User user) {
        Map<String, Object> claims = new ConcurrentHashMap<>();

        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles().name());

        String subject = user.getEmail();
        Date expiration = getExpiration(accessTokenExpirationMinutes);
        Key key = secretKey.getSecretKey();

        return createAccessToken(claims, subject, expiration, key);
    }

    public String delegateRefreshToken(User user) {
        String subject = String.valueOf(user.getEmail());
        Date expiration = getExpiration(refreshTokenExpirationMinutes);
        Key key = secretKey.getSecretKey();

        return createRefreshToken(subject, expiration, key);
    }

    private Date getExpiration(int tokenExpirationMinutes) {
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, tokenExpirationMinutes);
        return instance.getTime();
    }
}
