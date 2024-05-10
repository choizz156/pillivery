package com.team33.moduleapi.ui.order.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.team33.moduleapi.ui.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderSimpleResponse { // 주문 목록 조회

    private long orderId;
    private OrderStatus orderStatus;
    private int totalItems;
    private int expectPrice;
    private boolean subscription;
    private ItemSimpleResponseDto item;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    private OrderSimpleResponse(
        long orderId,
        OrderStatus orderStatus,
        int totalItems,
        int expectPrice,
        boolean subscription,
        ItemSimpleResponseDto item,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
    ) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalItems = totalItems;
        this.expectPrice = expectPrice;
        this.subscription = subscription;
        this.item = item;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static List<OrderSimpleResponse> toList(List<Order> orders) {
        return orders.stream()
            .map(OrderSimpleResponse::of)
            .collect(Collectors.toUnmodifiableList());
    }

    private static OrderSimpleResponse of(Order order) {
        return OrderSimpleResponse.builder()
            .orderId(order.getId())
            .orderStatus(order.getOrderStatus())
            .totalItems(order.getTotalItems())
            .expectPrice(order.getOrderPrice().getTotalPrice())
            .subscription(order.isSubscription())
            .item(ItemSimpleResponseDto.of(order.getFirstItem()))
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
