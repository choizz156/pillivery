package com.team33.modulecore.core.cart.dto;

import com.team33.modulecore.core.cart.domain.ItemVO;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscriptionContext {
	private final Long cartId;
	private final ItemVO item;
	private final SubscriptionInfo subscriptionInfo;
	int quantity;

	@Builder
	public SubscriptionContext(Long cartId, ItemVO item, SubscriptionInfo subscriptionInfo, int quantity) {
		this.cartId = cartId;
		this.item = item;
		this.subscriptionInfo = subscriptionInfo;
		this.quantity = quantity;
	}
}
