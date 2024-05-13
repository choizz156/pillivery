package com.team33.modulecore.cart.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.cart.domain.entity.Cart;

public interface CartRepository extends Repository<Cart, Long> {
	void save(Cart cart);
	Optional<Cart> findById(long id);
}
