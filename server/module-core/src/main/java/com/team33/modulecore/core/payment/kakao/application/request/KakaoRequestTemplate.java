package com.team33.modulecore.core.payment.kakao.application.request;

import java.util.Map;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KakaoRequestTemplate {

	private final PaymentClient<KakaoApiRequestResponse> kakaoRequestClient;

	private static final String READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";

	public abstract Map<String, Object> getRequestParams(Order order);

	public KakaoApiRequestResponse request(Order order) {
		Map<String, Object> requestParams = getRequestParams(order);

		return kakaoRequestClient.send(requestParams, READY_URL);
	}
}
