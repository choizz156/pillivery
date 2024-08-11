package com.team33.modulecore.core.cart.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team33.modulecore.core.common.BaseEntity;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_item")
@Entity
public class CartItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_item_id")
	private Long id;

	private int totalQuantity;

	@Embedded
	private SubscriptionInfo subscriptionInfo;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "cart_id")
	private Cart cart;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	@Builder
	public CartItem(
		Long id,
		int totalQuantity,
		SubscriptionInfo subscriptionInfo,
		Item item
	) {
		this.id = id;
		this.totalQuantity = totalQuantity;
		this.subscriptionInfo = subscriptionInfo;
		this.item = item;
	}

	public static CartItem of(Item item, int totalQuantity, SubscriptionInfo subscriptionInfo) {
		return CartItem.builder()
			.totalQuantity(totalQuantity)
			.item(item)
			.subscriptionInfo(subscriptionInfo)
			.build();
	}

	public static CartItem of(Item item, int totalQuantity) {
		return CartItem.builder()
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

	public void addCart(Cart cart) {
		this.cart = cart;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"totalQuantity = " + totalQuantity + ", " +
			"subscriptionInfo = " + subscriptionInfo + ")";
	}

	public void remove(Cart cart) {
		this.cart = null;
	}
}
