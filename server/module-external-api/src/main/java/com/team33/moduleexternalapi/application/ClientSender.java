package com.team33.moduleexternalapi.application;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.KakaoHeader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ClientSender {

	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;

	public <T> T send(Map<String, Object> params, String url, Class<T> responseClass) throws JsonProcessingException {

		String kakaoRequest = objectMapper.writeValueAsString(params);
		var entity = new HttpEntity<>(kakaoRequest, KakaoHeader.HTTP_HEADERS.getHeaders());

		ResponseEntity<T> exchange =
			restTemplate.exchange(url, HttpMethod.POST, entity, responseClass);

		checkSuccess(exchange);

		return exchange.getBody();
	}

	private void checkSuccess(ResponseEntity<?> exchange) {
		if (!exchange.getStatusCode().is2xxSuccessful()) {
			throw new PaymentApiException("kakao api fail");
		}
	}
}
