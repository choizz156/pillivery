package com.team33.modulecore.core.payment.kakao.application.request;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.request.Request;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

@Service
public class KakaoOneTimeRequestDispatcher
	extends KakaoRequestTemplate<Order>
	implements Request<KakaoApiRequestResponse, Order> {

	private final ParameterProvider parameterProvider;

	public KakaoOneTimeRequestDispatcher(
		PaymentClient<KakaoApiRequestResponse> kakaoRequestClient,
		ParameterProvider parameterProvider
	) {

		super(kakaoRequestClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoApiRequestResponse request(Order order) {

		return super.request(order);
	}

	@Override
	public Map<String, Object> getRequestParams(Order order) {

		return parameterProvider.getOneTimePaymentRequestParams(order);
	}
}
