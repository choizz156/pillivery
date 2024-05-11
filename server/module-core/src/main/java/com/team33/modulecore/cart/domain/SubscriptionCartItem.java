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
	private int totalQuantity;

	@Embedded
	private SubscriptionInfo subscriptionInfo;

	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;

	@Builder
	public SubscriptionCartItem(
		int totalQuantity,
		SubscriptionInfo subscriptionInfo,
		Item item
	) {
		this.totalQuantity = totalQuantity;
		this.subscriptionInfo = subscriptionInfo;
		this.item = item;
	}

	public static SubscriptionCartItem of(Item item, int totalQuantity, SubscriptionInfo subscriptionInfo) {
		return SubscriptionCartItem.builder()
			.totalQuantity(totalQuantity)
			.item(item)
			.subscriptionInfo(subscriptionInfo)
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
}
