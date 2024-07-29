package com.team33.modulequartz.subscription.domain;

import com.team33.modulecore.order.domain.OrderItem;

import lombok.Getter;

@Getter
public class PaymentDateUpdatedEvent {

	private final OrderItem orderItem;

	public PaymentDateUpdatedEvent(OrderItem orderItem) {
		this.orderItem = orderItem;
	}
}
