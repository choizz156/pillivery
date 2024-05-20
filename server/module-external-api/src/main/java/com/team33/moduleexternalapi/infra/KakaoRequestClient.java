package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoRequestClient implements PaymentClient<KakaoRequestResponse> {
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;

	@SneakyThrows
	@Override
	public KakaoRequestResponse send(Map<String, String> params, String url) {
		String kakaoRequest = objectMapper.writeValueAsString(params);

		HttpEntity<String> kakaoRequestEntity
			= new HttpEntity<>(kakaoRequest, KakaoHeader.HTTP_HEADERS.getHeaders());
		ResponseEntity<KakaoRequestResponse> exchange = restTemplate.exchange(url, HttpMethod.POST, kakaoRequestEntity,
			KakaoRequestResponse.class);

		if (!exchange.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException("kakao request fail");
		}

		return exchange.getBody();
	}
}
