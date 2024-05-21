package com.team33.moduleexternalapi.infra;

import static com.team33.moduleexternalapi.infra.KakaoHeader.*;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Component
public class KakaoApproveClient implements PaymentClient<KakaoApproveResponse> {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@SneakyThrows
	@Override
	public KakaoApproveResponse send(Map<String, String> params, String url) {
		String approveBody = objectMapper.writeValueAsString(params);
		var entity = new HttpEntity<>(approveBody, HTTP_HEADERS.getHeaders());

		ResponseEntity<KakaoApproveResponse> exchange = restTemplate.exchange(url, HttpMethod.POST, entity,
			KakaoApproveResponse.class);

		checkSuccess(exchange);

		return exchange.getBody();
	}

	private void checkSuccess(ResponseEntity<KakaoApproveResponse> exchange) {
		if (!exchange.getStatusCode().is2xxSuccessful()) {
			throw new PaymentApiException("Kakao Approve API Error");
		}
	}
}
