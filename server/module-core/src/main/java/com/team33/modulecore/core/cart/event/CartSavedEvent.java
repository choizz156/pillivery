package com.team33.modulecore.core.cart.event;

import com.team33.modulecore.core.cart.domain.CartVO;

import lombok.Getter;

@Getter
public class CartSavedEvent {

	private final Long id;
	private final CartVO expiredCartVO;

	public CartSavedEvent(Long id, CartVO expiredCartVO) {
		this.id = id;
		this.expiredCartVO = expiredCartVO;
	}
}

