package com.team33.modulecore.core.cart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCart;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class CartService {

	private final CartRepository cartRepository;

	public Long createNormalCart() {
		NormalCart normalCart = NormalCart.create();
		cartRepository.save(normalCart);

		return normalCart.getId();
	}

	public Long createSubsCart() {
		SubscriptionCart subscriptionCart = SubscriptionCart.create();
		cartRepository.save(subscriptionCart);

		return subscriptionCart.getId();
	}

	public void deleteCart(Long normalCartId, Long subscriptionCartId) {
		cartRepository.deleteById(normalCartId);
		cartRepository.deleteById(subscriptionCartId);
	}
}
