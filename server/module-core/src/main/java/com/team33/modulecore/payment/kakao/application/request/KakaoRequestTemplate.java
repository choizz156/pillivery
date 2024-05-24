package com.team33.modulecore.payment.kakao.application.request;

import java.util.Map;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KakaoRequestTemplate {

	private final PaymentClient<KakaoRequestResponse> KakaoRequestClient;
	private static final String READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";

	public abstract Map<String, Object> getRequestParams(Order order);

	public KakaoRequestResponse request(Order order) {
		Map<String, Object> requestParams = getRequestParams(order);

		return KakaoRequestClient.send(requestParams, READY_URL);
	}
}
