package com.team33.modulebatch.domain.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.team33.modulebatch.step.SubscriptionOrderVO;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class ErrorItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderId;

	private String idempotentKey;

	private LocalDate nextPaymentDate;

	private LocalDate lastPaymentDate;

	private String status;

	@Builder
	private ErrorItem(
		Long id,
		String orderId,
		String idempotentKey,
		LocalDate nextPaymentDate,
		LocalDate lastPaymentDate,
		String status
	) {
		this.id = id;
		this.orderId = orderId;
		this.idempotentKey = idempotentKey;
		this.nextPaymentDate = nextPaymentDate;
		this.lastPaymentDate = lastPaymentDate;
		this.status = status;
	}

	public static ErrorItem of(SubscriptionOrderVO orderVO) {
		return ErrorItem.builder()
			.orderId(orderVO.getSubscriptionOrderId().toString())
			.idempotentKey(orderVO.getIdempotencyKey())
			.nextPaymentDate(orderVO.getNextPaymentDate())
			.lastPaymentDate(orderVO.getLastPaymentDate())
			.status("ERROR")
			.build();
	}

}
