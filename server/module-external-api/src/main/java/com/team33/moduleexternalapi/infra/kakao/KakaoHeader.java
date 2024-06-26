package com.team33.moduleexternalapi.infra.kakao;

import java.util.function.Supplier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KakaoHeader {
	HTTP_HEADERS(
		() -> {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("Authorization",  "SECRET_KEY DEV9F204C96DFE6655F42DCE22C77CF4CC4E3BD5");
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			return httpHeaders;
		}
	);

	private final Supplier<HttpHeaders> httpHeaders;

	public HttpHeaders getHeaders() {
		return httpHeaders.get();
	}
}
