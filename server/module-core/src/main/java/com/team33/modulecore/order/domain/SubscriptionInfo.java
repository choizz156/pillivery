package com.team33.modulecore.order.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

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

	private ZonedDateTime nextDelivery;

	private ZonedDateTime paymentDay;

	@Builder
	public SubscriptionInfo(int period, boolean subscription, ZonedDateTime nextDelivery, ZonedDateTime paymentDay) {
		this.period = period;
		this.subscription = subscription;
		this.nextDelivery = nextDelivery;
		this.paymentDay = paymentDay;
	}

	public static SubscriptionInfo of(boolean isSubscription, int period) {

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

		return SubscriptionInfo.builder()
			.subscription(isSubscription)
			.period(period)
			.nextDelivery(now.plusDays(period))
			.build();
	}

	public void addPeriod(int period) {
		this.period += period;
	}

	public void cancelSubscription() {
		this.subscription = false;
	}

	public void addPaymentDay(ZonedDateTime paymentDay) {
		this.paymentDay = paymentDay;
	}

	public void applyNextPayment() {
		this.nextDelivery = paymentDay.plusDays(period);
	}
}
