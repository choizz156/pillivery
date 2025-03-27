package com.team33.modulecore.core.payment.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.core.payment.domain.entity.SubscriptionOrder;

public interface SubscriptionOrderRepository extends Repository<SubscriptionOrder, Long> {

	Optional<SubscriptionOrder> findById(long subscriptionOrderId);

	SubscriptionOrder save(SubscriptionOrder subscriptionOrder);

	List<SubscriptionOrder> saveAll(Iterable<SubscriptionOrder> entities);
}
