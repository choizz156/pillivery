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
			httpHeaders.add("Authorization",  "SECRET_KEY DEV9F204C96DFE6655F42DCE22C77CF4CC4E3BD5");
			httpHeaders.add("Content-Type","application/json");
			return httpHeaders;
		}
	);

	private final Supplier<HttpHeaders> httpHeaders;

	public HttpHeaders getHeaders() {
		return httpHeaders.get();
	}
}
