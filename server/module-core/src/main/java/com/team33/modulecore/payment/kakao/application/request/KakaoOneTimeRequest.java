package com.team33.modulecore.payment.kakao.application.request;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.Request;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoApiRequestResponse;

@Service
public class KakaoOneTimeRequest
	extends KakaoRequestTemplate
	implements Request<KakaoApiRequestResponse> {

	private final ParameterProvider parameterProvider;

	public KakaoOneTimeRequest(
		PaymentClient<KakaoApiRequestResponse> webClientRequestClient,
		ParameterProvider parameterProvider
	) {
		super(webClientRequestClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoApiRequestResponse request(Order order) {
		return super.request(order);
	}

	@Override
	public Map<String, Object> getRequestParams(Order order) {
		return parameterProvider.getOneTimeReqsParams(order);
	}
}
