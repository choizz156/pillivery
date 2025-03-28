package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.approve.ApproveFacade;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionRegisteredEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade implements ApproveFacade<KakaoApproveResponse, KakaoApproveRequest> {

	private final SubscriptionApproveService<KakaoApproveResponse, SubscriptionOrder> kakaoSubsApproveService;
	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final OrderFindHelper orderFindHelper;
	private final SubscriptionOrderService subscriptionOrderService;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public KakaoApproveResponse approveInitially(KakaoApproveRequest approveRequest) {

		Long orderId = approveRequest.getOrderId();
		Order order = orderFindHelper.findOrder(orderId);

		KakaoApproveResponse response = kakaoOneTimeApproveService.approveOneTime(approveRequest);

		if (order.isSubscription()) {
			eventPublisher.publishEvent(new SubscriptionRegisteredEvent(order.getId()));
		}

		return response;
	}

	@Override
	public KakaoApproveResponse approveSubscription(Long subscriptionOrderId) {

		SubscriptionOrder subscriptionOrder = subscriptionOrderService.findById(subscriptionOrderId);
		return kakaoSubsApproveService.approveSubscribe(subscriptionOrder);
	}

	public KakaoApproveResponse approveSid(ApproveRequest approveRequest) {

		return kakaoSubsApproveService.approveInitially(approveRequest);
	}

}

