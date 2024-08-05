package com.team33.modulecore.core.cart.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.entity.CartItem;
import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.core.item.domain.entity.Item;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class NormalCartItemService {

	private final CartRepository cartRepository;

	public NormalCart findCart(Long cartId) {
		return cartRepository.findNormalCartById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	public void addItem(Long cartId, Item item, int quantity) {
		NormalCart normalCart = findCart(cartId);
		CartItem cartItem = CartItem.of(item, quantity);
		normalCart.addNormalItem(cartItem);
	}
}
