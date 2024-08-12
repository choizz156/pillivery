package com.team33.modulecore.core.cart.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team33.modulecore.core.cart.domain.entity.CartEntity;
import com.team33.modulecore.core.cart.domain.entity.NormalCartEntity;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
	Optional<NormalCartEntity> findNormalCartById(Long cartId);
	Optional<SubscriptionCartEntity> findSubscriptionCartById(Long cartId);
}
