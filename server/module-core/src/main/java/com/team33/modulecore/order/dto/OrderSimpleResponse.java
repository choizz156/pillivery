package com.team33.modulecore.order.dto;

import com.team33.modulecore.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.orderitem.domain.OrderItem;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderSimpleResponse { // 주문 목록 조회 용도

    private long itemOrderId;
    private int quantity;
    private int period;
    private boolean subscription;
    private ItemSimpleResponseDto item;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    private OrderSimpleResponse(long itemOrderId, int quantity, int period, boolean subscription,
        ItemSimpleResponseDto item, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.itemOrderId = itemOrderId;
        this.quantity = quantity;
        this.period = period;
        this.subscription = subscription;
        this.item = item;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrderSimpleResponse of(OrderItem orderItem) {
        return OrderSimpleResponse.builder()
            .itemOrderId(orderItem.getOrderItemId())
            .quantity(orderItem.getQuantity())
            .period(orderItem.getPeriod())
            .subscription(orderItem.isSubscription())
            .item(ItemSimpleResponseDto.of(orderItem.getItem()))
            .createdAt(orderItem.getCreatedAt())
            .updatedAt(orderItem.getUpdatedAt())
            .build();
    }
}
