package com.team33.modulecore.core.cart.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CartItemVO {
	private Long id;

	private int totalQuantity;

	private SubscriptionInfo subscriptionInfo;

	@JsonBackReference
	private CartVO cart;

	private ItemVO item;

	@Builder
	public CartItemVO(
		Long id,
		int totalQuantity,
		SubscriptionInfo subscriptionInfo,
		ItemVO item
	) {
		this.id = id;
		this.totalQuantity = totalQuantity;
		this.subscriptionInfo = subscriptionInfo;
		this.item = item;
	}

	public static CartItemVO of(ItemVO item, int totalQuantity, SubscriptionInfo subscriptionInfo) {
		return CartItemVO.builder()
			.totalQuantity(totalQuantity)
			.item(item)
			.subscriptionInfo(subscriptionInfo)
			.build();
	}

	public static CartItemVO of(ItemVO item, int totalQuantity) {
		return CartItemVO.builder()
			.totalQuantity(totalQuantity)
			.item(item)
			.subscriptionInfo(SubscriptionInfo.of(false, 0))
			.build();
	}

	public int getPeriod() {
		return subscriptionInfo.getPeriod();
	}

	public boolean getSubscription() {
		return subscriptionInfo.isSubscription();
	}

	public void changeQuantity(int quantity) {
		this.totalQuantity = quantity;
	}

	public void changePeriod(int period) {
		this.subscriptionInfo = SubscriptionInfo.of(true, period);
	}

	public void addCart(CartVO cart) {
		this.cart = cart;
	}

	public void remove(CartVO cart) {
		this.cart = null;
	}
}
