package com.team33.modulecore.order.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class SubscriptionItemInfo {

    @ColumnDefault("0")
    private int period;

    @Column(name = "subscription")
    private boolean isSubscription;

    @Builder
    public SubscriptionItemInfo(int period, boolean isSubscription) {
        this.period = period;
        this.isSubscription = isSubscription;
    }

    public static SubscriptionItemInfo of(boolean subscription, int period) {
        return SubscriptionItemInfo.builder()
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
