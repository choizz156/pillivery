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

	private ZonedDateTime nextPaymentDay;

	private ZonedDateTime lastPaymentDay;

	@Builder(toBuilder = true)
	public SubscriptionInfo(int period, boolean subscription, ZonedDateTime nextPaymentDay,
		ZonedDateTime lastPaymentDay) {
		this.period = period;
		this.subscription = subscription;
		this.nextPaymentDay = nextPaymentDay;
		this.lastPaymentDay = lastPaymentDay;
	}

	public static SubscriptionInfo of(boolean isSubscription, int period) {

		return SubscriptionInfo.builder()
			.subscription(isSubscription)
			.period(period)
			.nextPaymentDay(ZonedDateTime.now())
			.lastPaymentDay(ZonedDateTime.now())
			.build();
	}


	public void updatePaymentDay(ZonedDateTime paymentDay) {
		this.lastPaymentDay = paymentDay;
		updateNextPaymentDay();
	}

	public void changePeriod(@Positive int newPeriod) {
		this.period = newPeriod;
		updateNextPaymentDay();
	}

	public void cancelSubscription() {
		this.subscription = false;
	}

	private void updateNextPaymentDay() {
		this.nextPaymentDay = lastPaymentDay.plusDays(this.period);
	}

}
