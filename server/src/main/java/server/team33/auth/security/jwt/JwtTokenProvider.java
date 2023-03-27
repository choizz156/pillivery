package server.team33.auth.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import server.team33.user.entity.User;

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

    public String createAccessToken(Map<String, Object> claims, String subject, Date expiration,
        Key key) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setExpiration(expiration)
            .setIssuedAt(Calendar.getInstance().getTime())
            .signWith(key)
            .compact();
    }

    public String extractJws(HttpServletRequest request) {
        return request.getHeader("Authorization").replace("Bearer ", "");
    }

    public Map<String, Object> getJwsBody(HttpServletRequest request) {
        String jws = extractJws(request);
        Key key = secretKey.getSecretKey();
        return getJws(jws, key).getBody();
    }

    public Jws<Claims> getJws(String jws, Key key) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jws);
    }

    public void addTokenInResponse(HttpServletResponse response, User user){
        String accessToken = delegateAccessToken(user);
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("userId", String.valueOf(user.getUserId()));
    }

    public String delegateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles().name());

        String subject = user.getEmail();
        Date expiration = getExpiration(accessTokenExpirationMinutes);
        Key key = secretKey.getSecretKey();

        return createAccessToken(claims, subject, expiration, key);
    }

    private Date getExpiration(int tokenExpirationMinutes) {
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, tokenExpirationMinutes);
        return instance.getTime();
    }
}
