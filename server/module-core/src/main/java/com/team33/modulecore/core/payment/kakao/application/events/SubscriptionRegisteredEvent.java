package com.team33.modulecore.core.payment.kakao.application.events;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SubscriptionRegisteredEvent {

	private final Order order;
	private final KakaoApproveRequest approveRequest;
}
