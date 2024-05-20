package com.team33.moduleexternalapi.infra;

import java.util.Map;

import org.springframework.http.HttpEntity;
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

		// Map<String, Object> requestBody = new HashMap<>();
		// requestBody.put("cid", "TC0ONETIME");
		// requestBody.put("partner_order_id", "partner_order_id");
		// requestBody.put("partner_user_id", "partner_user_id");
		// requestBody.put("item_name", "초코파이");
		// requestBody.put("quantity", "1");
		// requestBody.put("total_amount", "2200");
		// requestBody.put("vat_amount", "200");
		// requestBody.put("tax_free_amount", "0");
		// requestBody.put("approval_url", "http://localhost:8080/success");
		// requestBody.put("fail_url", "http://localhost:8080/kakao/fail");
		// requestBody.put("cancel_url", "http://localhost:8080/cancel");

		String kakaoRequest = objectMapper.writeValueAsString(params);

		HttpEntity<String> kakaoRequestEntity
			= new HttpEntity<>(kakaoRequest, KakaoHeader.HTTP_HEADERS.getHeaders());

		return restTemplate.postForObject(url, kakaoRequestEntity, KakaoRequestResponse.class);
	}
}
