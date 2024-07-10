package com.team33.modulequartz.subscription.domain;

import com.team33.modulecore.order.domain.OrderItem;

import lombok.Getter;

@Getter
public class PaymentUpdatedEvent {

	private final OrderItem orderItem;

	public PaymentUpdatedEvent(OrderItem orderItem) {
		this.orderItem = orderItem;
	}
}
