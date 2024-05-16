package com.team33.modulecore.payment.kakao.application.request;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.NormalRequest;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;
import com.team33.moduleexternalapi.domain.PaymentClient;

@Service
public class KakaoNormalRequest
	extends KakaoRequestTemplate
	implements NormalRequest<KakaoRequestResponse> {

	private final ParameterProvider parameterProvider;

	public KakaoNormalRequest(
		PaymentClient<KakaoRequestResponse> KakaoRequestClient,
		ParameterProvider parameterProvider
	) {
		super(KakaoRequestClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoRequestResponse requestOneTime(Order order) {
		return super.request(order);
	}

	@Override
	public MultiValueMap<String, String> getRequestParams(Order order) {
		return parameterProvider.getSubscriptionReqsParams(order);
	}
}
