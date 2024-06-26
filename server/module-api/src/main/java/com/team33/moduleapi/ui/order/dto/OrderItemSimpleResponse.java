package com.team33.moduleapi.ui.order.dto;

import java.time.ZonedDateTime;

import com.team33.moduleapi.ui.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.order.domain.OrderItem;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderItemSimpleResponse {

    private long orderItemId;
    private int quantity;
    private int period;
    private boolean subscription;
    private ItemSimpleResponseDto item;

    @Builder
    public OrderItemSimpleResponse(
        long orderItemId,
        int quantity,
        int period,
        boolean subscription,
        ItemSimpleResponseDto item
    ) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.period = period;
        this.subscription = subscription;
        this.item = item;
    }

    public static OrderItemSimpleResponse of(OrderItem orderItem) {
        return OrderItemSimpleResponse.builder()
            .orderItemId(orderItem.getId())
            .quantity(orderItem.getQuantity())
            .period(orderItem.getPeriod())
            .subscription(orderItem.isSubscription())
            .item(ItemSimpleResponseDto.of(orderItem.getItem()))
            .build();
    }
}
