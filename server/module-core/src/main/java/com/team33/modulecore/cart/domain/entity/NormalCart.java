package com.team33.modulecore.cart.domain.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.team33.modulecore.item.domain.entity.Item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("normal")
public class NormalCart extends Cart {

	public static NormalCart create() {
		return new NormalCart();
	}

	public void addNormalItem(CartItem cartItem) {
		cartItem.addCart(this);
		super.cartItems.add(cartItem);

		Item item = cartItem.getItem();
		int quantity = cartItem.getTotalQuantity();
		super.price = super.price.addPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}
}
