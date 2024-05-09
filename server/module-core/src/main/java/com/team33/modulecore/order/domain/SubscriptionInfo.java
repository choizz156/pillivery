package com.team33.modulecore.order.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.ColumnDefault;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class SubscriptionInfo {

    @ColumnDefault("0")
    private int period;

    @Column(name = "subscription")
    private boolean isSubscription;

    @Builder
    public SubscriptionInfo(int period, boolean isSubscription) {
        this.period = period;
        this.isSubscription = isSubscription;
    }

    public static SubscriptionInfo of(boolean subscription, int period) {
        return SubscriptionInfo.builder()
            .isSubscription(subscription)
            .period(period)
            .build();
    }

    public void addPeriod(int period) {
        this.period += period;
    }

    public void cancelSubscription() {
        this.isSubscription = false;
    }
}
