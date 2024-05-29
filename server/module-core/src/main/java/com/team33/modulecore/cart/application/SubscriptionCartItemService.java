package com.team33.modulecore.cart.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.team33.modulecore.cart.SubscriptionContext;
import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.domain.entity.CartItem;
import com.team33.modulecore.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.cart.domain.repository.CartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionCartItemService {

	private final CartRepository cartRepository;

	public SubscriptionCart findCart(Long cartId) {
		return cartRepository.findSubscriptionCartById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));
	}

	public void addSubscriptionItem(Long cartId, SubscriptionContext subscriptionContext) {
		SubscriptionCart cart = findCart(cartId);

		CartItem cartItem = CartItem.of(
			subscriptionContext.getItem(),
			subscriptionContext.getQuantity(),
			subscriptionContext.getSubscriptionInfo()
		);
		cart.addSubscriptionItem(cartItem);
	}

	public void changePeriod(Long cartId, Long cartItemId, int period) {
		SubscriptionCart cart = findCart(cartId);

		CartItem subscriptionCartItem = getCartItem(cartItemId, cart);

		subscriptionCartItem.changePeriod(period);
	}

	private CartItem getCartItem(Long cartItemId, Cart cart) {

		return cart.getCartItems().stream()
			.filter(
				cartItem -> cartItem.getItem().getId().equals(cartItemId)
			)
			.findFirst()
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_ITEM_NOT_FOUND));
	}

}
