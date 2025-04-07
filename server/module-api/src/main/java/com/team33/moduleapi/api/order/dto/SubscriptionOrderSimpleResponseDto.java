package com.team33.moduleapi.api.order.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.team33.moduleapi.api.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SubscriptionOrderSimpleResponseDto { // 주문 목록 조회

    private long subscriptionOrderId;
    private OrderStatus orderStatus;
    private int totalItems;
    private int expectPrice;
    private boolean subscription;
    private ItemSimpleResponseDto firstItem;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private OrderItemSimpleResponse orderItemSimpleResponse;

    @Builder
    private SubscriptionOrderSimpleResponseDto(
        long subscriptionOrderId,
        OrderStatus orderStatus,
        int totalItems,
        int expectPrice,
        boolean subscription,
        ItemSimpleResponseDto item,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt, OrderItemSimpleResponse orderItemSimpleResponse
	) {
        this.subscriptionOrderId = subscriptionOrderId;
        this.orderStatus = orderStatus;
        this.totalItems = totalItems;
        this.expectPrice = expectPrice;
        this.subscription = subscription;
        this.firstItem = item;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
		this.orderItemSimpleResponse = orderItemSimpleResponse;
	}

    public static List<SubscriptionOrderSimpleResponseDto> toList(List<SubscriptionOrder> orders) {
        return orders.stream()
            .map(SubscriptionOrderSimpleResponseDto::of)
            .collect(Collectors.toUnmodifiableList());
    }

    private static SubscriptionOrderSimpleResponseDto of(SubscriptionOrder order) {
        return SubscriptionOrderSimpleResponseDto.builder()
            .subscriptionOrderId(order.getId())
            .orderStatus(order.getOrderCommonInfo().getOrderStatus())
            .expectPrice(order.getOrderCommonInfo().getTotalPrice())
            .subscription(order.isSubscription())
            .item(ItemSimpleResponseDto.of(order.getOrderItem().getItem()))
            .orderItemSimpleResponse(OrderItemSimpleResponse.fromSubscriptionOrder(order.getOrderItem()))
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
