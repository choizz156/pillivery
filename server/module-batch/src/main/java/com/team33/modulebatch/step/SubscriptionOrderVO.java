package com.team33.modulebatch.step;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriptionOrderVO {

	private Long subscriptionOrderId;
	private boolean subscription;
	private LocalDate nextPaymentDate;
	private LocalDate lastPaymentDate;
	private String idempotencyKey;

	@Builder
	public SubscriptionOrderVO(Long subscriptionOrderId, boolean subscription, LocalDate paymentDate) {

		this.subscriptionOrderId = subscriptionOrderId;
		this.subscription = subscription;
		this.nextPaymentDate = paymentDate;
	}
}
