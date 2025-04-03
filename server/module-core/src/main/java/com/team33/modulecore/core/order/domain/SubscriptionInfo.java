package com.team33.modulecore.core.order.domain;

import java.time.ZonedDateTime;

import javax.persistence.Embeddable;
import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Embeddable
public class SubscriptionInfo {

	private int period;

	private boolean subscription;

	private ZonedDateTime nextPaymentDate;

	private ZonedDateTime lastPaymentDate;

	@Builder(toBuilder = true)
	public SubscriptionInfo(int period, boolean subscription, ZonedDateTime nextPaymentDate,
			ZonedDateTime lastPaymentDate) {
		this.period = period;
		this.subscription = subscription;
		this.nextPaymentDate = nextPaymentDate;
		this.lastPaymentDate = lastPaymentDate;
	}

	public static SubscriptionInfo of(boolean isSubscription, int period) {

		return SubscriptionInfo.builder()
				.subscription(isSubscription)
				.period(period)
				.nextPaymentDate(ZonedDateTime.now())
				.lastPaymentDate(ZonedDateTime.now())
				.build();
	}

	public void updatePaymentDate(ZonedDateTime paymentDate) {
		this.lastPaymentDate = paymentDate;
		updateNextPaymentDate();
	}

	public void changePeriod(@Positive int newPeriod) {
		this.period = newPeriod;
		updateNextPaymentDate();
	}

	public void cancelSubscription() {
		this.subscription = false;
	}

	private void updateNextPaymentDate() {
		this.nextPaymentDate = lastPaymentDate.plusDays(this.period);
	}

}
