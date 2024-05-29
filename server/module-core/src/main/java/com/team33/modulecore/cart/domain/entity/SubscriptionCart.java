package com.team33.modulecore.cart.domain.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.team33.modulecore.item.domain.entity.Item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("subscription")
@Entity
public class SubscriptionCart extends Cart {

	public static SubscriptionCart create() {
		return new SubscriptionCart();
	}

	public void addSubscriptionItem(CartItem cartItem) {
		cartItem.addCart(this);
		super.cartItems.add(cartItem);

		Item item = cartItem.getItem();
		super.price = super.price.addPriceInfo(
			item.getRealPrice(),
			item.getDiscountPrice(),
			cartItem.getTotalQuantity()
		);
	}
}
