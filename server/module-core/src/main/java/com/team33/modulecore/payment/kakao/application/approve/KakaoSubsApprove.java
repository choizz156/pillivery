package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.approve.SubscriptionApprove;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.dto.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.application.PaymentClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoSubsApprove
	implements SubscriptionApprove<KakaoApiApproveResponse> {

	private final PaymentClient<KakaoApiApproveResponse> kakaoApproveClient;
	private final ParameterProvider parameterProvider;
	private static final String KAKAO_SUBSCRIPTION_URL = "https://open-api.kakaopay.com/online/v11/payment/subscription";

	@Override
	public KakaoApiApproveResponse approveSubscription(Order order) {
		var subscriptionApproveParams = parameterProvider.getSubscriptionApproveParams(order);

		return kakaoApproveClient.send(subscriptionApproveParams, KAKAO_SUBSCRIPTION_URL);
	}
}
