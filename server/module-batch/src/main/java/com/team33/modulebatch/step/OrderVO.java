package com.team33.modulebatch.step;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderVO {

	private Long orderId;
	private boolean subscription;
	private LocalDate nextPaymentDate;
	private LocalDate lastPaymentDate;
	private String idempotencyKey;

	@Builder
	public OrderVO(Long orderId, boolean subscription, LocalDate paymentDate) {
		this.orderId = orderId;
		this.subscription = subscription;
		this.nextPaymentDate = paymentDate;
	}

}
