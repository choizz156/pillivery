package com.team33.moduleapi.ui.order.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
public class OrderDetailResponse {

    private long orderId;
    private String name;
    private String city;
    private String detailAddress;
    private String phone;
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

    @Builder
    private OrderDetailResponse(
        long orderId,
        String name,
        String city,
        String detailAddress,
        String phone,
        int totalItems,
        int totalPrice,
        int totalDiscountPrice,
        int expectPrice,
        boolean subscription,
        List<OrderItemSimpleResponse> itemOrders,
        OrderStatus orderStatus,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        int totalQuantity
    ) {
        this.orderId = orderId;
        this.name = name;
        this.city = city;
        this.detailAddress = detailAddress;
        this.phone = phone;
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
    }

    public static OrderDetailResponse of(Order order) {
        return OrderDetailResponse.builder()
            .city(order.getOrdererCity())
            .detailAddress(order.getOrdererDetailAddress())
            .orderId(order.getId())
            .name(order.getName())
            .phone(order.getPhone())
            .totalItems(order.getTotalItems())
            .totalPrice(order.getOrderPrice().getTotalPrice())
            .totalDiscountPrice(order.getOrderPrice().getTotalDiscountPrice())
            .expectPrice(order.getOrderPrice().getTotalPrice())
            .subscription(order.isSubscription())
            .itemOrders(
                    order.getOrderItems()
                        .stream()
                        .map(OrderItemSimpleResponse::of)
                        .collect(Collectors.toList())
            )
            .orderStatus(order.getOrderStatus())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
