package com.team33.modulecore.cart.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.SubscriptionInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class SubscriptionCartItem extends BaseEntity {

	@Column(nullable = false)
	private int quantity;

	@Embedded
	private SubscriptionInfo subscriptionInfo;

	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;

	@Builder
	public SubscriptionCartItem(
		int quantity,
		SubscriptionInfo subscriptionInfo,
		Item item
	) {
		this.quantity = quantity;
		this.subscriptionInfo = subscriptionInfo;
		this.item = item;
	}

	public static SubscriptionCartItem of(Item item, int quantity, SubscriptionInfo subscriptionInfo) {
		return SubscriptionCartItem.builder()
			.quantity(quantity)
			.item(item)
			.subscriptionInfo(subscriptionInfo)
			.build();
	}

	// 장바구니에 같은 상품을 또 담을 경우 수량만 증가
	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}

	public int getPeriod() {
		return subscriptionInfo.getPeriod();
	}

	public boolean getSubscription() {
		return subscriptionInfo.isSubscription();
	}
}
