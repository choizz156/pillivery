package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.application.SubscriptionOrderService;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionRegisteredEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionHandler {

	private final SubscriptionOrderService subscriptionOrderService;
	private final SubscriptionApproveService<KakaoApproveResponse> kakaoSubsApproveService;
	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final ApplicationEventPublisher eventPublisher;

	public KakaoApproveResponse handleSingleItem(Order order, KakaoApproveRequest approveRequest) {

		SubscriptionOrder subscriptionOrder = subscriptionOrderService.createSingle(order);

		KakaoApproveRequest subscriptionRequest = KakaoApproveRequest.toSubscriptionRequest(
			approveRequest,
			subscriptionOrder.getId()
		);

		return kakaoSubsApproveService.approveInitially(subscriptionRequest);
	}

	public KakaoApproveResponse handleMultiItem(Order order, KakaoApproveRequest approveRequest) {
		KakaoApproveResponse kakaoApproveResponse = kakaoOneTimeApproveService.approveOneTime(approveRequest);


		eventPublisher.publishEvent(new SubscriptionRegisteredEvent(order, approveRequest));

		return kakaoApproveResponse;
	}

}
