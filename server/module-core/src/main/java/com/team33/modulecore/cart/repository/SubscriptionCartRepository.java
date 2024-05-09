package com.team33.modulecore.cart.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.cart.domain.SubscriptionCart;

public interface SubscriptionCartRepository extends Repository<SubscriptionCart, Long> {

	Optional<SubscriptionCart> findById(long id);

	void save(SubscriptionCart subscriptionCart);
	//    Cart findByUser(User user);
}
