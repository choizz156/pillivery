package com.team33.moduleapi.ui.order.dto;

import java.time.ZonedDateTime;

import com.team33.moduleapi.ui.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.order.domain.entity.OrderItem;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderItemSimpleResponse {

    private final long orderItemId;
    private final int quantity;
    private final int period;
    private final boolean subscription;
    private final ZonedDateTime nextPaymentDay;
    private final ItemSimpleResponseDto item;

    @Builder
    public OrderItemSimpleResponse(
        long orderItemId,
        int quantity,
        int period,
        boolean subscription,
        ZonedDateTime nextPaymentDay,
        ItemSimpleResponseDto item
    ) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.period = period;
        this.subscription = subscription;
		this.nextPaymentDay = nextPaymentDay;
		this.item = item;
    }

    public static OrderItemSimpleResponse of(OrderItem orderItem) {
        return OrderItemSimpleResponse.builder()
            .orderItemId(orderItem.getId())
            .quantity(orderItem.getQuantity())
            .period(orderItem.getPeriod())
            .nextPaymentDay(orderItem.getPaymentDay())
            .subscription(orderItem.isSubscription())
            .item(ItemSimpleResponseDto.of(orderItem.getItem()))
            .build();
    }
}
