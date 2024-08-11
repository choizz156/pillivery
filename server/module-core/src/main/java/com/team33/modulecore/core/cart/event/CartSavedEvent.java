package com.team33.modulecore.core.cart.event;

import com.team33.modulecore.core.cart.domain.entity.Cart;

import lombok.Getter;

@Getter
public class CartSavedEvent {

	private final Long id;
	private final Cart cart;

	public CartSavedEvent(Long id, Cart expiredCart) {
		this.id = id;
		this.cart = expiredCart;
	}

}

