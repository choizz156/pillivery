package com.team33.moduleapi.api.order.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.team33.moduleapi.api.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OrderSimpleResponseDto { // 주문 목록 조회

    private long orderId;
    private OrderStatus orderStatus;
    private int totalItems;
    private int expectPrice;
    private boolean subscription;
    private ItemSimpleResponseDto firstItem;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    private OrderSimpleResponseDto(
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
        this.firstItem = item;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static List<OrderSimpleResponseDto> toList(List<Order> orders) {
        return orders.stream()
            .map(OrderSimpleResponseDto::of)
            .collect(Collectors.toUnmodifiableList());
    }

    private static OrderSimpleResponseDto of(Order order) {
        return OrderSimpleResponseDto.builder()
            .orderId(order.getId())
            .orderStatus(order.getOrderStatus())
            .totalItems(order.getTotalItemsCount())
            .expectPrice(order.getPrice().getTotalPrice())
            .subscription(order.isSubscription())
            .item(ItemSimpleResponseDto.of(order.getFirstItem()))
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
