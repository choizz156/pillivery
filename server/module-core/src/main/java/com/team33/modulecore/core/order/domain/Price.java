package com.team33.modulecore.core.order.domain;

import java.util.List;
import javax.persistence.Embeddable;

import com.team33.modulecore.core.order.domain.entity.OrderItem;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Embeddable
public class Price {

    private int totalPrice;

    private int totalDiscountPrice;

    @Builder
    public Price(int totalPrice, int totalDiscountPrice) {
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
    }

    public Price(List<OrderItem> orderItems) {
        int totalPrice = countTotalPrice(orderItems);
        int totalDiscountPrice = countTotalDiscountPrice(orderItems);
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
    }

    private int countTotalPrice(List<OrderItem> orderItems) {

        if (orderItems.isEmpty()) {
            return 0;
        }

        return orderItems.stream()
            .mapToInt(orderItem ->
                orderItem.getQuantity() * orderItem.getItem().getRealPrice()
            )
            .sum();
    }

    private int countTotalDiscountPrice(List<OrderItem> orderItems) {

        if (orderItems.isEmpty()) {
            return 0;
        }

        return orderItems.stream()
            .mapToInt(
                orderItem ->
                    orderItem.getQuantity() * orderItem.getItem().getDiscountPrice()
            )
            .sum();
    }
}
