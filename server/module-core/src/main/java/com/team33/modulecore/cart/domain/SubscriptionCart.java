package com.team33.modulecore.cart.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.itemcart.domain.SubscriptionCartItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SubscriptionCart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subs_cart_id")
	private Long id;

	private int totalItemCount;

	private int totalPrice;

	private int totalDiscountPrice;

	@ElementCollection
	@CollectionTable(name = "subs_cart_item", joinColumns = @JoinColumn(name = "cart_id"))
	private List<SubscriptionCartItem> subscriptionCartItems = new ArrayList<>();

	public void addItem(Item item, int quantity, SubscriptionInfo subscriptionInfo) {
		SubscriptionCartItem.builder().item(item).build();
		this.subscriptionCartItems.add(SubscriptionCartItem.of(item, quantity, subscriptionInfo));
	}

	public static SubscriptionCart create() {
		return new SubscriptionCart();
	}

	public void changeTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void changeTotalItems(int totalItems) {
		this.totalItemCount = totalItems;
	}
}
