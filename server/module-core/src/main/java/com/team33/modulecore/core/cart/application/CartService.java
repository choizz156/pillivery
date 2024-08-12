package com.team33.modulecore.core.cart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.cart.domain.entity.NormalCartEntity;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCartEntity;
import com.team33.modulecore.core.cart.domain.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class CartService {

	private final CartRepository cartRepository;

	public Long createNormalCart() {
		NormalCartEntity normalCart = NormalCartEntity.create();
		cartRepository.save(normalCart);

		return normalCart.getId();
	}

	public Long createSubsCart() {
		SubscriptionCartEntity subscriptionCart = SubscriptionCartEntity.create();
		cartRepository.save(subscriptionCart);

		return subscriptionCart.getId();
	}

	public void deleteCart(Long normalCartId, Long subscriptionCartId) {
		cartRepository.deleteById(normalCartId);
		cartRepository.deleteById(subscriptionCartId);
	}
}
