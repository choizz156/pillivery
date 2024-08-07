package com.team33.modulecore.core.cart.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.core.cart.domain.entity.Cart;
import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCart;

public interface CartRepository extends Repository<Cart, Long> {

	void save(Cart cart);
	Optional<Cart> findById(Long id);
	Optional<NormalCart> findNormalCartById(Long cartId);
	Optional<SubscriptionCart> findSubscriptionCartById(Long cartId);

	void deleteById(Long cartId);
}
