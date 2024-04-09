package com.team33.modulecore.domain.order.entity;

import java.util.List;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Price {

    private int totalPrice;

    private int totalDiscountPrice;

    private int expectPrice; // 실제 결제 금액 (정가 - 할인가)

    @Builder
    public Price(int totalPrice, int totalDiscountPrice, int expectPrice) {
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.expectPrice = expectPrice;
    }

    public Price (List<OrderItem> orderItems){
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

        int totalPrice = 0;

        for (OrderItem orderItem : orderItems) {
            int quantity = orderItem.getQuantity();
            int price = orderItem.getItem().getPrice();
            totalPrice += (quantity * price);
        }

        return totalPrice;
    }

    private int countDiscountTotalPrice(List<OrderItem> orderItems) {

        if (orderItems == null) {
            return 0;
        }

        int totalDiscountPrice = 0;

        for (OrderItem orderItem : orderItems) {
            int quantity = orderItem.getQuantity();
            int price = orderItem.getItem().getPrice();
            int discountRate = orderItem.getItem().getDiscountRate();
            totalDiscountPrice += (quantity * price * discountRate / 100);
        }

        return totalDiscountPrice;
    }
}
