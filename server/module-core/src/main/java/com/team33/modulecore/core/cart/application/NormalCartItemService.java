package com.team33.modulecore.core.cart.application;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NormalCartItemService {
	private final CartRepository cartRepository;
	private final MemoryCartClient memoryCartClient;

	public NormalCart findCart(String key, long cartId) {
		return getCart(key, cartId);
	}

	private NormalCart getCart(String key, long cartId) {
		NormalCart normalCart = (NormalCart)memoryCartClient.getCart(key);

		if (normalCart == null) {
			normalCart = cartRepository.findNormalCartById(cartId)
				.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

			memoryCartClient.saveCart(key, normalCart);

			return normalCart;
		}

		return normalCart;
	}
}
