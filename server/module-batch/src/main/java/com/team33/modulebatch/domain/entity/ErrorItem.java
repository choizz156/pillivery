package com.team33.modulebatch.domain.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.team33.modulebatch.domain.ErrorStatus;
import com.team33.modulebatch.step.SubscriptionOrderVO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "error_item")
@Entity
public class ErrorItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "error_item_id")
	private Long id;

	private Long subscriptionOrderId;

	private String idempotentKey;

	private LocalDate nextPaymentDate;

	private LocalDate lastPaymentDate;

	@Enumerated(EnumType.STRING)
	private ErrorStatus status;

	@Builder
	private ErrorItem(
		Long id,
		Long subscriptionOrderId,
		String idempotentKey,
		LocalDate nextPaymentDate,
		LocalDate lastPaymentDate,
		ErrorStatus status
	) {
		this.id = id;
		this.subscriptionOrderId = subscriptionOrderId;
		this.idempotentKey = idempotentKey;
		this.nextPaymentDate = nextPaymentDate;
		this.lastPaymentDate = lastPaymentDate;
		this.status = status;
	}

	public static ErrorItem byServerError(SubscriptionOrderVO subscriptionOrderVO) {
		return ErrorItem.builder()
			.subscriptionOrderId(subscriptionOrderVO.getSubscriptionOrderId())
			.idempotentKey(subscriptionOrderVO.getIdempotencyKey())
			.nextPaymentDate(subscriptionOrderVO.getNextPaymentDate())
			.lastPaymentDate(subscriptionOrderVO.getLastPaymentDate())
			.status(ErrorStatus.ERROR)
			.build();
	}
}
