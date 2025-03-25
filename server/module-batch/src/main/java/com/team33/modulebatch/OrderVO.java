package com.team33.modulebatch;

import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderVO {

	private Long orderId;
	private boolean subscription;
	private Date nextPaymentDate;

	@Builder
	public OrderVO(Long orderId, boolean subscription, Date paymentDate) {
		this.orderId = orderId;
		this.subscription = subscription;
		this.nextPaymentDate = paymentDate;
	}

}
