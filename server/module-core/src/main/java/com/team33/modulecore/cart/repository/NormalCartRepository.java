package com.team33.modulecore.cart.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.cart.domain.entity.NormalCart;

public interface NormalCartRepository extends Repository<NormalCart, Long> {
	void save(NormalCart cart);
	Optional<NormalCart> findById(long id);
}
