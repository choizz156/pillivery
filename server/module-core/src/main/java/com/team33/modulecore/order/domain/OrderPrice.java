package com.team33.modulecore.order.domain;

import java.util.List;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Embeddable
public class OrderPrice {

    private int totalPrice;

    private int totalDiscountPrice;

    @Builder
    public OrderPrice(int totalPrice, int totalDiscountPrice) {
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
    }

    public OrderPrice(List<OrderItem> orderItems) {
        int totalPrice = countTotalPrice(orderItems);
        int totalDiscountPrice = countTotalDiscountPrice(orderItems);
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
    }

    private int countTotalPrice(List<OrderItem> orderItems) {

        if (orderItems == null) {
            return 0;
        }

        return orderItems.stream()
            .mapToInt(orderItem ->
                orderItem.getQuantity() * orderItem.getItem().getItemPrice().getRealPrice()
            )
            .sum();
    }

    private int countTotalDiscountPrice(List<OrderItem> orderItems) {

        if (orderItems == null) {
            return 0;
        }

        return orderItems.stream()
            .mapToInt(
                orderItem ->
                    orderItem.getQuantity() * orderItem.getItem().getItemPrice().getDiscountPrice()
            )
            .sum();
    }
}
