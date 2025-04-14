package com.team33.modulecore.core.cart.vo;

import java.util.ArrayList;
import java.util.List;

import com.team33.modulecore.core.cart.domain.CartPrice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@Getter
public abstract class CartVO{

	private Long id;
	protected CartPrice price = new CartPrice(0, 0, 0);
	protected List<CartItemVO> cartItems = new ArrayList<>();

	public CartVO(Long id, CartPrice cartPrice) {
		this.id = id;
		this.price = cartPrice;
	}

	public abstract void addCartItems(CartItemVO cartItemVO);

	public void removeCartItem(CartItemVO removedItem) {
		this.cartItems.remove(removedItem);

		ItemVO item = removedItem.getItem();
		int quantity = removedItem.getTotalQuantity();

		this.price = this.price.subtractPriceInfo(item.getRealPrice(), item.getDiscountPrice(), quantity);
	}

	public void changeCartItemQuantity(CartItemVO cartItemVO, int quantity) {
		ItemVO item = cartItemVO.getItem();

		this.price = CartPrice.of(item.getRealPrice(), item.getDiscountPrice(), quantity);
		cartItemVO.changeQuantity(quantity);
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
