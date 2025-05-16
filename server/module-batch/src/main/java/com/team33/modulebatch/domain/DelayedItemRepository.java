package com.team33.modulebatch.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team33.modulebatch.domain.entity.DelayedItem;

public interface DelayedItemRepository extends JpaRepository<DelayedItem, Long> {

	List<DelayedItem> findByDelayedPaymentDateAndRetryCount(LocalDate date, int retryCount);

	boolean existsDelayedItemBySubscriptionOrderIdAndStatus(long subscriptionOrderId, ErrorStatus status);
}
