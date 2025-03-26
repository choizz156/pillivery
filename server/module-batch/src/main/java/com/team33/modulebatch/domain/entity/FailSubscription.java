package com.team33.modulebatch.domain.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.team33.modulebatch.step.OrderVO;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class FailSubscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderId;

	private String idempotentKey;

	private LocalDate nextPaymentDate;

	private LocalDate lastPaymentDate;

	@Builder
	private FailSubscription(
		Long id,
		String orderId,
		String idempotentKey,
		LocalDate nextPaymentDate,
		LocalDate lastPaymentDate
	) {
		this.id = id;
		this.orderId = orderId;
		this.idempotentKey = idempotentKey;
		this.nextPaymentDate = nextPaymentDate;
		this.lastPaymentDate = lastPaymentDate;
	}

	public static FailSubscription of(OrderVO orderVO) {
		return FailSubscription.builder()
			.orderId(orderVO.getOrderId().toString())
			.idempotentKey(orderVO.getIdempotencyKey())
			.nextPaymentDate(orderVO.getNextPaymentDate())
			.lastPaymentDate(orderVO.getLastPaymentDate())
			.build();
	}

}
