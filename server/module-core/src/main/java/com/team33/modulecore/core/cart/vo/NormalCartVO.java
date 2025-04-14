package com.team33.modulecore.core.cart.vo;

import com.team33.modulecore.core.cart.domain.CartPrice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
public class NormalCartVO extends CartVO {

	public NormalCartVO(Long id, CartPrice cartPrice) {
		super(id, cartPrice);
	}

	@Override
	public void addCartItems(CartItemVO cartItemVO) {
		this.getCartItems().add(cartItemVO);
		cartItemVO.addCart(this);

		ItemVO item = cartItemVO.getItem();
		int quantity = cartItemVO.getTotalQuantity();
		super.price = super.price.addPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

	public void addNormalItem(CartItemVO cartItem) {
		cartItem.addCart(this);
		super.cartItems.add(cartItem);

		ItemVO item = cartItem.getItem();
		int quantity = cartItem.getTotalQuantity();
		super.price = super.price.addPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}
}
