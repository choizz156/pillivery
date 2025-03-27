package com.team33.modulecore.core.payment.kakao.application.request;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.request.Request;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

@Service
public class KakaoSubsRequestDispatcher
	extends KakaoRequestTemplate<SubscriptionOrder>
	implements Request<KakaoApiRequestResponse, SubscriptionOrder> {

	private final ParameterProvider parameterProvider;

	public KakaoSubsRequestDispatcher(
		PaymentClient<KakaoApiRequestResponse> KakaoRequestClient,
		ParameterProvider parameterProvider
	) {
		super(KakaoRequestClient);
		this.parameterProvider = parameterProvider;
	}

	@Override
	public KakaoApiRequestResponse request(SubscriptionOrder subscriptionOrder) {
		return super.request(subscriptionOrder);
	}

	@Override
	public Map<String, Object> getRequestParams(SubscriptionOrder subscriptionOrder) {
		return parameterProvider.getSubscriptionPaymentRequestParams(subscriptionOrder);
	}

}
