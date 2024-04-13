package com.team33.modulecore.domain.order.value;

import com.team33.modulecore.domain.order.entity.OrderItem;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Embeddable
public class Price {

    @Column(nullable = false)
    private int totalPrice;

    private int totalDiscountPrice;

    private int expectPrice; // 실제 결제 금액 (정가 - 할인가)

    @Builder
    public Price(int totalPrice, int totalDiscountPrice, int expectPrice) {
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.expectPrice = expectPrice;
    }

    public Price(List<OrderItem> orderItems) {
        int totalPrice = countTotalPrice(orderItems);
        int totalDiscountPrice = countDiscountTotalPrice(orderItems);

        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.expectPrice = totalPrice - totalDiscountPrice;
    }

    private int countTotalPrice(List<OrderItem> orderItems) {

        if (orderItems == null) {
            return 0;
        }

        return orderItems.stream().mapToInt(oi -> oi.getQuantity() * oi.getItem().getPrice()).sum();
    }

    private int countDiscountTotalPrice(List<OrderItem> orderItems) {

        if (orderItems == null) {
            return 0;
        }

        return orderItems.stream().mapToInt(
            oi -> oi.getQuantity() * oi.getItem().getPrice() * oi.getItem().getDiscountRate()/100
        ).sum();
    }
}
