package com.team33.modulecore.cart.domain.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.team33.modulecore.cart.domain.CartPrice;
import com.team33.modulecore.cart.domain.NormalCartItem;
import com.team33.modulecore.cart.domain.SubscriptionCartItem;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.SubscriptionInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long id;

	@Embedded
	private CartPrice price = new CartPrice(0, 0, 0);

	@ElementCollection
	@CollectionTable(name = "normal_cart_item", joinColumns = @JoinColumn(name = "cart_id"))
	private Set<NormalCartItem> normalCartItems = new HashSet<>();

	@ElementCollection
	@CollectionTable(name = "subs_cart_item", joinColumns = @JoinColumn(name = "cart_id"))
	private Set<SubscriptionCartItem> subscriptionCartItems = new HashSet<>();

	public static Cart create() {
		return new Cart();
	}

	public void addNormalItem(Item item, int quantity) {
		this.normalCartItems.add(NormalCartItem.of(item, quantity));
		this.price = this.price.addPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

	public void removeNormalCartItem(NormalCartItem removedItem) {
		this.normalCartItems.remove(removedItem);

		Item item = removedItem.getItem();
		int quantity = removedItem.getTotalQuantity();

		this.price = this.price.subtractPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

	public void addSubscriptionItem(Item item, int quantity, SubscriptionInfo subscriptionInfo) {
		this.subscriptionCartItems.add(SubscriptionCartItem.of(item, quantity, subscriptionInfo));
		this.price = this.price.addPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

	public void removeSubscriptionCartItem(SubscriptionCartItem removedItem) {
		this.subscriptionCartItems.remove(removedItem);

		Item item = removedItem.getItem();
		int quantity = removedItem.getTotalQuantity();

		this.price = this.price.subtractPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

	public void changeNormalCartItemQuantity(NormalCartItem normalCartItem, int quantity) {
		Item item = normalCartItem.getItem();

		this.price = CartPrice.of(item.getRealPrice(), item.getDiscountPrice(), quantity);
		normalCartItem.changeQuantity(quantity);
	}

	public void changeSubscriptionCartItemQuantity(SubscriptionCartItem subscriptionCartItem, int quantity) {
		Item item = subscriptionCartItem.getItem();

		this.price = CartPrice.of(item.getRealPrice(), item.getDiscountPrice(), quantity);
		subscriptionCartItem.changeQuantity(quantity);
	}

	public int getTotalDiscountPrice() {
		return this.price.getTotalDiscountPrice();
	}

	public int getTotalPrice() {
		return this.price.getTotalPrice();
	}

	public int getTotalItemCount() {
		return this.price.getTotalItemCount();
	}

	public int getExpectedPrice() {
		return this.price.getTotalPrice() - this.price.getTotalDiscountPrice();
	}
}
