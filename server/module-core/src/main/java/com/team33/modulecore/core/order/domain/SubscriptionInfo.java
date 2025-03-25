package com.team33.modulecore.core.order.domain;

import java.time.ZonedDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@Getter
@Access(AccessType.FIELD)
public class SubscriptionInfo {

	private int period;

	@Column(name = "subscription")
	private boolean subscription;

	private ZonedDateTime nextPaymentDay;

	private ZonedDateTime paymentDay;

	@Builder
	public SubscriptionInfo(int period, boolean subscription, ZonedDateTime nextPaymentDay, ZonedDateTime paymentDay) {
		this.period = period;
		this.subscription = subscription;
		this.nextPaymentDay = nextPaymentDay;
		this.paymentDay = paymentDay;
	}

	public static SubscriptionInfo of(boolean isSubscription, int period) {

		return SubscriptionInfo.builder()
			.subscription(isSubscription)
			.period(period)
			.nextPaymentDay(ZonedDateTime.now())
			.paymentDay(ZonedDateTime.now())
			.build();
	}

	public void cancelSubscription() {
		this.subscription = false;
		this.nextPaymentDay = null;
	}

	public void updatePaymentDay(ZonedDateTime paymentDay) {
		this.paymentDay = paymentDay;
		updateNextPaymentDay();
	}

	public void changePeriod(@Positive int newPeriod) {
		this.period = newPeriod;
		updateNextPaymentDay();
	}

	private void updateNextPaymentDay() {
		this.nextPaymentDay = paymentDay.plusDays(this.period);
	}
}
