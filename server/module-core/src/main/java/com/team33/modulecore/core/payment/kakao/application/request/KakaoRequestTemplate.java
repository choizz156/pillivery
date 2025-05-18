package com.team33.modulecore.core.payment.kakao.application.request;

import java.util.Map;

import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class KakaoRequestTemplate<T> {

	private static final String KAKAO_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
	private final PaymentClient<KakaoApiRequestResponse> kakaoRequestClient;

	public KakaoApiRequestResponse request(T requestObject) {

		Map<String, Object> requestParams = getRequestParams(requestObject);

		return kakaoRequestClient.send(requestParams, KAKAO_READY_URL);
	}

	public abstract Map<String, Object> getRequestParams(T requestObject);

}
