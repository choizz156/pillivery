package com.team33.modulecore.order.dto;

import com.team33.modulecore.order.domain.Receiver;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderContext {
	private boolean isOrderedCart;
	private boolean isSubscription;
	private Long userId;
	private Receiver receiver;

	@Builder
	public OrderContext(boolean isOrderedCart, boolean isSubscription, Long userId, Receiver receiver) {
		this.isOrderedCart = isOrderedCart;
		this.isSubscription = isSubscription;
		this.userId = userId;
		this.receiver = receiver;
	}
}
