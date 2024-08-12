package com.team33.modulecore.core.cart.domain;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
public class NormalCartVO extends CartVO {

	public NormalCartVO(Long id, CartPrice cartPrice, List<CartItemVO> list) {
		super(id, cartPrice, list);
	}

	public void addNormalItem(CartItemVO cartItem) {
		cartItem.addCart(this);
		super.cartItems.add(cartItem);

		ItemVO item = cartItem.getItem();
		int quantity = cartItem.getTotalQuantity();
		super.price = super.price.addPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}
}
