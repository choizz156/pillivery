package com.team33.moduleapi.controller.order.dto;

import com.team33.moduleapi.controller.order.dto.OrderItemDto.SimpleResponse;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.entity.OrderStatus;
import com.team33.modulecore.global.response.MultiResponseDto;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;

@Data
public class OrderDetailResponse {

    private long orderId;
    private String name;
    private String address;
    private String detailAddress;
    private String phone;
    private int totalItems;
    private int totalPrice;
    private int totalDiscountPrice;
    private int expectPrice;
    private boolean subscription;
    private MultiResponseDto<SimpleResponse> itemOrders; // 페이지네이션 X
    private OrderStatus orderStatus;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private int totalQuantity;

    @Builder
    private OrderDetailResponse(
        long orderId,
        String name,
        String address,
        String detailAddress,
        String phone,
        int totalItems,
        int totalPrice,
        int totalDiscountPrice,
        int expectPrice,
        boolean subscription,
        MultiResponseDto<SimpleResponse> itemOrders,
        OrderStatus orderStatus,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        int totalQuantity
    ) {
        this.orderId = orderId;
        this.name = name;
        this.address = address;
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
            .address(order.getAddress().getCity())
            .detailAddress(order.getAddress().getDetailAddress())
            .orderId(order.getOrderId())
            .name(order.getName())
            .phone(order.getPhone())
            .totalItems(order.getTotalItems())
            .totalPrice(order.getPrice().getTotalPrice())
            .totalDiscountPrice(order.getPrice().getTotalDiscountPrice())
            .expectPrice(order.getPrice().getExpectPrice())
            .subscription(order.isSubscription())
            .itemOrders(new MultiResponseDto<>(
                order.getOrderItems().stream().map(SimpleResponse::of).collect(Collectors.toList()))
            )
            .orderStatus(order.getOrderStatus())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
