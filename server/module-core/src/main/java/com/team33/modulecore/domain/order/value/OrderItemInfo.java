package com.team33.modulecore.domain.order.value;

import com.team33.modulecore.domain.order.dto.OrderDto.Post;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor
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

    public static OrderItemInfo of(Post dto) {
        return OrderItemInfo.builder()
            .subscription(dto.isSubscription())
            .period(dto.getPeriod())
            .quantity(dto.getQuantity())
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
