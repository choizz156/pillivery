package com.team33.modulecore.payment.kakao.application.request;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.Request;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

@Service
public class KakaoSubsRequest
	extends KakaoRequestTemplate
	implements Request<KakaoRequestResponse>
{

	private final ParameterProvider parameterProvider;

	public KakaoSubsRequest(
		PaymentClient<KakaoRequestResponse> KakaoRequestClient,
		ParameterProvider parameterProvider
	) {
		super(KakaoRequestClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoRequestResponse request(Order order) {
		return super.request(order);
	}

	@Override
	public Map<String, Object> getRequestParams(Order order) {
		return parameterProvider.getSubscriptionReqsParams(order);
	}
}
