package com.team33.modulecore.core.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

	REQUEST("주문 요청"),
	COMPLETE("주문 완료"),
	REFUND("주문 취소"),
	SUBSCRIPTION("구독 중"),
	SUBSCRIBE_CANCEL("구독 취소"),
	SUBSCRIBE_PAYMENT_FAIL("구독 결제 실패");

	private final String description;
}
