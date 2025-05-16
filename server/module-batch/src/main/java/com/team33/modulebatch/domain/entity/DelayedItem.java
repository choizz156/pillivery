package com.team33.modulebatch.domain.entity;

import java.time.LocalDate;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

	private LocalDate delayedPaymentDate;

	private String reason;

	@Enumerated(EnumType.STRING)
	private ErrorStatus status;

	@Builder
	private DelayedItem(
		Long id,
		Long subscriptionOrderId,
		Long retryCount,
		LocalDate delayedPaymentDate,
		String reason,
		ErrorStatus status
	) {

		this.id = id;
		this.subscriptionOrderId = subscriptionOrderId;
		this.retryCount = retryCount;
		this.delayedPaymentDate = delayedPaymentDate;
		this.reason = reason;
		this.status = status;
	}

	public static DelayedItem of(long subscriptionOrderId, String reason) {
		return DelayedItem.builder()
			.subscriptionOrderId(subscriptionOrderId)
			.retryCount(0L)
			.status(ErrorStatus.DELAYED)
			.reason(reason)
			.delayedPaymentDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
			.build();
	}
}

