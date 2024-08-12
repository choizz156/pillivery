package com.team33.modulecore.core.cart.domain.vo;

import java.util.List;

import com.team33.modulecore.core.cart.domain.CartPrice;
import com.team33.modulecore.core.item.domain.entity.Item;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NormalCartVO extends CartVO {

	public NormalCartVO(Long id, CartPrice cartPrice, List<CartItemVO> list) {
		super(id, cartPrice, list);
	}

	public void addNormalItem(CartItemVO cartItem) {
		cartItem.addCart(this);
		super.cartItems.add(cartItem);

		Item item = cartItem.getItem();
		int quantity = cartItem.getTotalQuantity();
		super.price = super.price.addPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

}
