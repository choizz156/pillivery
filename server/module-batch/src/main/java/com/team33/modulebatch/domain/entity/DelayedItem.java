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
import javax.persistence.Transient;

import com.team33.modulebatch.domain.ErrorStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "delayed_item")
@Entity
public class DelayedItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "delayed_item_id")
	private Long id;

	private Long subscriptionOrderId;

	private Long retryCount;

	private LocalDate originalPaymentDate;

	private String reason;

	@Transient
	private String idempotencyKey;

	@Enumerated(EnumType.STRING)
	private ErrorStatus status;

	@Builder
	private DelayedItem(
		Long id,
		Long subscriptionOrderId,
		Long retryCount,
		LocalDate originalPaymentDate,
		String reason,
		ErrorStatus status
	) {

		this.id = id;
		this.subscriptionOrderId = subscriptionOrderId;
		this.retryCount = retryCount;
		this.originalPaymentDate = originalPaymentDate;
		this.reason = reason;
		this.status = status;
	}

	public static DelayedItem of(long subscriptionOrderId, String reason, LocalDate now) {

		return DelayedItem.builder()
			.subscriptionOrderId(subscriptionOrderId)
			.retryCount(0L)
			.status(ErrorStatus.DELAYED)
			.reason(reason)
			.originalPaymentDate(now)
			.build();
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public void updateStatus(ErrorStatus status) {
		this.status = status;
	}

	public void addRetryCount(long count) {
		this.retryCount = count;
	}
}

