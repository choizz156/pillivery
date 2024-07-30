package com.team33.modulequartz.subscription.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.team33.modulecore.order.domain.OrderItem;

import lombok.Getter;

@Getter
public class PaymentDateUpdatedEvent {

	private final OrderItem orderItem;
	private final ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

	public PaymentDateUpdatedEvent(OrderItem orderItem) {
		this.orderItem = orderItem;
	}
}
