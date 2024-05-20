package com.team33.moduleexternalapi.infra;

import static com.team33.moduleexternalapi.infra.KakaoHeader.*;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KaKaoApproveResponse;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Component
public class KakaoApproveClient implements PaymentClient<KaKaoApproveResponse> {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@SneakyThrows
	@Override
	public KaKaoApproveResponse send(Map<String, String> params, String url) {
		String approveBody = objectMapper.writeValueAsString(params);
		var entity = new HttpEntity<>(approveBody, HTTP_HEADERS.getHeaders());

		return restTemplate.postForObject(
			url,
			entity,
			KaKaoApproveResponse.class
		);
	}
}
