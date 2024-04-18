package com.team33.modulecore.orderitem.domain;

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
public class OrderItemInfo {

    @Column(nullable = false)
    private int quantity;

    @ColumnDefault("0")
    private int period;

    @Column(name = "subscription")
    private boolean subscription;

    @Builder
    public OrderItemInfo(int quantity, int period, boolean subscription) {
        this.quantity = quantity;
        this.period = period;
        this.subscription = subscription;
    }

    public static OrderItemInfo of(int quantity, boolean subscription, int period) {
        return OrderItemInfo.builder()
            .subscription(subscription)
            .period(period)
            .quantity(quantity)
            .build();
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void addPeriod(int period) {
        this.period += period;
    }

    public void cancelSubscription() {
        this.subscription = false;
    }
}
