package com.team33.modulecore.core.order.dto;

import com.team33.modulecore.core.order.domain.Receiver;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderContext {
	private final boolean isOrderedCart;
	private final boolean isSubscription;
	private final Long userId;
	private final Receiver receiver;
	private final int period;

	@Builder
	public OrderContext(boolean isOrderedCart, boolean isSubscription, Long userId, Receiver receiver, int period) {
		this.isOrderedCart = isOrderedCart;
		this.isSubscription = isSubscription;
		this.userId = userId;
		this.receiver = receiver;
		this.period = period;
	}
}
