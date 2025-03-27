package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.approve.ApproveFacade;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionRegisteredEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade1 {

	private final SubscriptionApproveService<KakaoApproveResponse> kakaoSubsApproveService;
	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final OrderFindHelper orderFindHelper;
	private final ApplicationEventPublisher eventPublisher;

	public KakaoApproveResponse approveInitially(KakaoApproveRequest approveRequest) {
		Long orderId = approveRequest.getOrderId();
		Order order = orderFindHelper.findOrder(orderId);

		KakaoApproveResponse response = kakaoOneTimeApproveService.approveOneTime(approveRequest);

		if (order.isSubscription()) {
			eventPublisher.publishEvent(new SubscriptionRegisteredEvent(order.getId()));
		}

		return response;
	}

	public KakaoApproveResponse registerSid(KakaoApproveRequest request) {
		return kakaoSubsApproveService.approveInitially(request);
	}

	public KakaoApproveResponse approveSubscription(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		return kakaoSubsApproveService.approveSubscribe(order);
	}

}

