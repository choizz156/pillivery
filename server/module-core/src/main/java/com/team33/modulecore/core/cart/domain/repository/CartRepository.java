package com.team33.modulecore.core.cart.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team33.modulecore.core.cart.domain.entity.CartEntity;
import com.team33.modulecore.core.cart.domain.entity.NormalCartEntity;
import com.team33.modulecore.core.cart.domain.entity.SubscriptionCartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {

	@Query("SELECT c FROM CartEntity c WHERE c.id = :cartId AND TYPE(c) = NormalCartEntity")
	Optional<NormalCartEntity> findNormalCartById(@Param("cartId") Long cartId);

	@Query("SELECT c FROM CartEntity c WHERE c.id = :cartId AND TYPE(c) = SubscriptionCartEntity")
	Optional<SubscriptionCartEntity> findSubscriptionCartById(@Param("cartId") Long cartId);
}
