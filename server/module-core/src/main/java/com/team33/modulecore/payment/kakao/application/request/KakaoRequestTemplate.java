package com.team33.modulecore.payment.kakao.application.request;

import org.springframework.util.MultiValueMap;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;
import com.team33.moduleexternalapi.domain.PaymentClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KakaoRequestTemplate {

	private final PaymentClient<KakaoRequestResponse> KakaoRequestClient; // <KakaoRequestResponse>
	private static final String READY_URL = "https//kapi.kakao.com/v1/payment/ready";

	public abstract MultiValueMap<String, String> getRequestParams(Order order);

	public KakaoRequestResponse request(Order order) {
		MultiValueMap<String, String> requestParams = getRequestParams(order);

		return KakaoRequestClient.send(requestParams, READY_URL);
	}
}
