package com.team33.modulecore.orderitem.dto;

import com.team33.modulecore.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.orderitem.domain.OrderItem;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderItemSimpleResponse {

    private long itemOrderId;
    private int quantity;
    private int period;
    private boolean subscription;
    private ItemSimpleResponseDto item;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    public OrderItemSimpleResponse(
        long itemOrderId,
        int quantity,
        int period,
        boolean subscription,
        ItemSimpleResponseDto item,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
    ) {
        this.itemOrderId = itemOrderId;
        this.quantity = quantity;
        this.period = period;
        this.subscription = subscription;
        this.item = item;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrderItemSimpleResponse of(OrderItem orderItem) {
        return OrderItemSimpleResponse.builder()
            .itemOrderId(orderItem.getOrderItemId())
            .quantity(orderItem.getQuantity())
            .period(orderItem.getPeriod())
            .subscription(orderItem.isSubscription())
            .item(com.team33.modulecore.item.dto.ItemSimpleResponseDto.of(orderItem.getItem()))
            .createdAt(orderItem.getCreatedAt())
            .updatedAt(orderItem.getUpdatedAt())
            .build();
    }
}
