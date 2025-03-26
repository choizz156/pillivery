package com.team33.modulebatch.step;

import java.time.LocalDate;
import java.util.Date;

import javax.annotation.Nullable;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderVO {

	private Long orderId;
	private boolean subscription;
	private LocalDate nextPaymentDate;
	private String idempotencyKey;

	@Builder
	public OrderVO(Long orderId, boolean subscription, LocalDate paymentDate) {
		this.orderId = orderId;
		this.subscription = subscription;
		this.nextPaymentDate = paymentDate;
	}

}
