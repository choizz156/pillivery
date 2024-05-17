package com.team33.modulecore.cart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class CartService {

	private final CartRepository cartRepository;

	@Transactional(readOnly = true)
	public Cart findCart(Long cartId) {
		return cartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	public Cart create() {
		Cart cart = Cart.create();
		cartRepository.save(cart);
		return cart;
	}
}
