package com.team33.modulecore.cart.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.team33.modulecore.cart.domain.entity.Cart;
import com.team33.modulecore.cart.domain.entity.NormalCart;
import com.team33.modulecore.cart.domain.entity.SubscriptionCart;

public interface CartRepository extends Repository<Cart, Long> {

	void save(Cart cart);
	Optional<Cart> findById(Long id);
	Optional<NormalCart> findNormalCartById(@Param("cartId") Long cartId);
	Optional<SubscriptionCart> findSubscriptionCartById(Long cartId);

	void deleteById(Long cartId);
}
