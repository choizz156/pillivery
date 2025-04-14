package com.team33.modulecore.core.cart.vo;

import com.team33.modulecore.core.cart.domain.CartPrice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
public class SubscriptionCartVO extends CartVO {

	public SubscriptionCartVO(Long id, CartPrice cartPrice) {
		super(id, cartPrice);
	}

	public static SubscriptionCartVO create() {
		return new SubscriptionCartVO();
	}

	@Override
	public void addCartItems(CartItemVO cartItemVO) {
		this.getCartItems().add(cartItemVO);
		cartItemVO.addCart(this);

		ItemVO item = cartItemVO.getItem();
		int quantity = cartItemVO.getTotalQuantity();
		super.price = super.price.addPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
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
