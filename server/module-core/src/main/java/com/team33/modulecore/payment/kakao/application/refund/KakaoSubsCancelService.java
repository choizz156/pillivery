package com.team33.modulecore.payment.kakao.application.refund;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.cancel.CancelSubscriptionService;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoSubsCancelResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsCancelService implements CancelSubscriptionService<KakaoSubsCancelResponse> {

	private final static String CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive";

	private final PaymentClient<KakaoSubsCancelResponse> kakaoSubsCancelClient;
	private final OrderFindHelper orderFindHelper;
	private final ParameterProvider parameterProvider;

	@Override
	public KakaoSubsCancelResponse cancelSubscription(Long orderId) {

		Order order = orderFindHelper.findOrder(orderId);
		Map<String, Object> subsCancelParams = parameterProvider.getSubsCancelParams(order);

		return kakaoSubsCancelClient.send(subsCancelParams, CANCEL_URL);
	}
}
