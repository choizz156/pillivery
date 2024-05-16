package com.team33.moduleexternalapi.infra;

import java.util.function.Supplier;

import org.springframework.http.HttpHeaders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KakaoHeader {
	HTTP_HEADERS(
		() -> {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("Authorization", "KakaoAK 15fe252b3ce1d6da44b790e005f40964");
			httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			return httpHeaders;
		}
	);

	private final Supplier<HttpHeaders> httpHeaders;

	public HttpHeaders getHeaders() {
		return httpHeaders.get();
	}
}
