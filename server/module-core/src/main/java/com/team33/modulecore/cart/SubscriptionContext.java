package com.team33.modulecore.cart;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.SubscriptionInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscriptionContext {
	private Long cartId;
	private Item item;
	private SubscriptionInfo subscriptionInfo;
	int quantity;

	@Builder
	public SubscriptionContext(Long cartId, Item item, SubscriptionInfo subscriptionInfo, int quantity) {
		this.cartId = cartId;
		this.item = item;
		this.subscriptionInfo = subscriptionInfo;
		this.quantity = quantity;
	}
}
