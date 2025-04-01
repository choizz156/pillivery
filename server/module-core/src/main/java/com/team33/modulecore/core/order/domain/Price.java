package com.team33.modulecore.core.order.domain;

import java.util.List;

import javax.persistence.Embeddable;

import com.team33.modulecore.core.order.domain.entity.OrderItem;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Embeddable
public class Price {

    private int totalPrice;
    private int totalDiscountPrice;
    private int expectPrice;


    @Builder
    public Price(int totalPrice, int totalDiscountPrice, int expectPrice) {
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.expectPrice = expectPrice;
    }

    public Price(int totalPrice, int totalDiscountPrice) {
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.expectPrice = totalPrice - totalDiscountPrice;
    }

    public Price(List<OrderItem> orderItems) {
        this.totalPrice = countTotalPrice(orderItems);
        this.totalDiscountPrice = countTotalDiscountPrice(orderItems);
        this.expectPrice = this.totalPrice - this.totalDiscountPrice;
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
