package team33.modulecore.global.auth.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecretKey {
    @Getter
    @Value("${jwt.secret-key}")
    private String baseKey;

    public Key getSecretKey(){
        byte[] keyBytes = Decoders.BASE64.decode(getEncodeSecretKey(baseKey));
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String getEncodeSecretKey( String baseKey ){
        return Encoders.BASE64.encode(baseKey.getBytes(StandardCharsets.UTF_8));
    }
}
