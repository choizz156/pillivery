package com.team33.modulecore.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

	REQUEST("주문 요청"),
	COMPLETE("주문 완료"),
	Refund("주문 취소"),
	SUBSCRIBE("구독 중"),
	SUBSCRIBE_CANCEL("구독 취소");

	private final String description;
}
