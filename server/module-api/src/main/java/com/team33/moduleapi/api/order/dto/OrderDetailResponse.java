package com.team33.moduleapi.api.order.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.Receiver;
import com.team33.modulecore.core.order.domain.entity.Order;

import lombok.Builder;
import lombok.Data;

@Data
public class OrderDetailResponse {

    private long orderId;
    private int totalItems;
    private int totalPrice;
    private int totalDiscountPrice;
    private int expectPrice;
    private boolean subscription;
    private  List<OrderItemSimpleResponse> itemOrders;
    private OrderStatus orderStatus;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private int totalQuantity;
    private Receiver receiver;

    @Builder
    private OrderDetailResponse(
        long orderId,
        int totalItems,
        int totalPrice,
        int totalDiscountPrice,
        int expectPrice,
        boolean subscription,
        List<OrderItemSimpleResponse> itemOrders,
        OrderStatus orderStatus,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        int totalQuantity,
        Receiver receiver
    ) {
        this.orderId = orderId;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.expectPrice = expectPrice;
        this.subscription = subscription;
        this.itemOrders = itemOrders;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalQuantity = totalQuantity;
        this.receiver = receiver;
    }

    public static OrderDetailResponse fromOrder(Order order) {
        return OrderDetailResponse.builder()
            .receiver(order.getReceiver())
            .orderId(order.getId())
            .totalItems(order.getTotalItemsCount())
            .totalPrice(order.getPrice().getTotalPrice())
            .totalDiscountPrice(order.getPrice().getTotalDiscountPrice())
            .expectPrice(order.getExpectPrice())
            .subscription(order.isSubscription())
            .itemOrders(
                    order.getOrderItems()
                        .stream()
                        .map(OrderItemSimpleResponse::fromOrder)
                        .collect(Collectors.toList())
            )
            .orderStatus(order.getOrderStatus())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .totalQuantity(order.getTotalQuantity())
            .build();
    }
}
