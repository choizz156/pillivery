package com.team33.modulecore.core.cart.application;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubscriptionCartItemService {

	private final CartRepository cartRepository;
	private final MemoryCartClient memoryCartClient;

	public SubscriptionCart findCart(String key, Long cartId) {
		return getSubscriptionCart(cartId, key);
	}

	private SubscriptionCart getSubscriptionCart(Long cartId, String key) {
		SubscriptionCart cachedSubscriptionCart = (SubscriptionCart)memoryCartClient.getCart(key);

		if (cachedSubscriptionCart == null) {
			SubscriptionCart subscriptionCart = cartRepository.findSubscriptionCartById(cartId)
				.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

			memoryCartClient.saveCart(key, subscriptionCart);

			return subscriptionCart;
		}

		return cachedSubscriptionCart;
	}
}
