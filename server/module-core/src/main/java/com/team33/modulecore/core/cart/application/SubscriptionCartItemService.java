package com.team33.modulecore.core.cart.application;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;
import com.team33.modulecore.core.cart.dto.SubscriptionContext;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubscriptionCartItemService {

	private final CartRepository cartRepository;
	private final MemoryCartClient memoryCartClient;

	public void addSubscriptionItem(Long cartId, SubscriptionContext subscriptionContext) {
		memoryCartClient.addSubscriptionItem(CartKeySupplier.from(cartId), subscriptionContext);
	}

	public void changePeriod(Long cartId, Long cartItemId, int period) {
		memoryCartClient.changePeriod(CartKeySupplier.from(cartId), cartItemId, period);
	}

	public SubscriptionCart findCart(Long cartId) {
		String key = CartKeySupplier.from(cartId);
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
