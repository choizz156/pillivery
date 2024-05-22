package com.team33.modulecore.order.domain;

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

    @Builder
    public SubscriptionInfo(int period, boolean subscription) {
        this.period = period;
        this.subscription = subscription;
    }

    public static SubscriptionInfo of(boolean isSubscription, int period) {
        return SubscriptionInfo.builder()
            .subscription(isSubscription)
            .period(period)
            .build();
    }

    public void addPeriod(int period) {
        this.period += period;
    }

    public void cancelSubscription() {
        this.subscription = false;
    }
}
