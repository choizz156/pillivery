package com.team33.modulecore.core.cart.dto;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
public class SubscriptionCartVO extends CartVO {

	public SubscriptionCartVO(Long id, CartPrice cartPrice, List<CartItemVO> list) {
		super(id, cartPrice, list);
	}

	public static SubscriptionCartVO create() {
		return new SubscriptionCartVO();
	}

	public void addSubscriptionItem(CartItemVO cartItem) {
		cartItem.addCart(this);
		super.cartItems.add(cartItem);

		ItemVO item = cartItem.getItem();
		super.price = super.price.addPriceInfo(
			item.getRealPrice(),
			item.getDiscountPrice(),
			cartItem.getTotalQuantity()
		);
	}
}
