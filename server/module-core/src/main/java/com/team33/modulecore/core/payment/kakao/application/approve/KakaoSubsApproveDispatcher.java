package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApprove;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoSubsApproveDispatcher
	implements SubscriptionApprove<KakaoApiApproveResponse, SubscriptionOrder> {

	private static final String KAKAO_SUBSCRIPTION_URL = "https://open-api.kakaopay.com/online/v11/payment/subscription";
	private final PaymentClient<KakaoApiApproveResponse> kakaoApproveClient;
	private final ParameterProvider parameterProvider;

	@Override
	public KakaoApiApproveResponse approveSubscription(SubscriptionOrder subscriptionOrder) {
		var subscriptionApproveParams = parameterProvider.getSubscriptionPaymentApprovalParams(subscriptionOrder);

		return kakaoApproveClient.send(subscriptionApproveParams, KAKAO_SUBSCRIPTION_URL);
	}
}
