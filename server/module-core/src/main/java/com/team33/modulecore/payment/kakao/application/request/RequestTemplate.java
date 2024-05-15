package com.team33.modulecore.payment.kakao.application.request;

import static com.team33.modulecore.payment.kakao.application.KakaoHeader.*;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RequestTemplate {

	private final RestTemplate restTemplate;
	private static final String READY_URL = "https//kapi.kakao.com/v1/payment/ready";

	public abstract MultiValueMap<String, String> getRequestParams(Order order);

	public KakaoRequestResponse request(Order order) {
		MultiValueMap<String, String> requestParams = getRequestParams(order);
		return getResponseDtoAboutRequest(requestParams);
	}

	private KakaoRequestResponse getResponseDtoAboutRequest(
		MultiValueMap<String, String> params
	) {
		HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity
			= new HttpEntity<>(params, HTTP_HEADERS.getHeaders());

		return restTemplate.postForObject(READY_URL, kakaoRequestEntity, KakaoRequestResponse.class);
	}
}
