package server.team33.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public abstract class KaKaoPayService {

    private final HttpHeaders httpHeaders;

    public HttpHeaders getHeaders() {
        httpHeaders.add("Authorization", "KakaoAK 15fe252b3ce1d6da44b790e005f40964");
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        return httpHeaders;
    }
}
