package com.team33.modulecore.cart.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.cart.domain.entity.SubscriptionCart;

public interface SubscriptionCartRepository extends Repository<SubscriptionCart, Long> {

	void save(SubscriptionCart subscriptionCart);
	Optional<SubscriptionCart> findById(long id);
}
