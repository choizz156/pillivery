package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionRegisteredEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionHandler {

	private final OneTimeApproveService<KakaoApproveResponse> kakaoOneTimeApproveService;
	private final ApplicationEventPublisher eventPublisher;

	public KakaoApproveResponse handle(Order order, KakaoApproveRequest approveRequest) {

		eventPublisher.publishEvent(new SubscriptionRegisteredEvent(order.getId()));

		return kakaoApproveResponse;
	}

}
