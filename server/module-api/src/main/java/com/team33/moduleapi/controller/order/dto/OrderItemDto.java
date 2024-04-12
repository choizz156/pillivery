package com.team33.moduleapi.controller.order.dto;

import com.team33.modulecore.domain.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.domain.order.entity.OrderItem;
import java.time.ZonedDateTime;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class OrderItemDto {

    @Getter
    public static class Post {

        private long itemId;
        @Min(value = 1, message = "수량은 1개 이상 선택해주세요.")
        private int quantity;
        private int period;
        private boolean subscription;
        private String address;
        private String detailAddress;
    }

    @Getter
    public static class SimpleResponse { // 주문 목록 조회 용도

        private long itemOrderId;
        private int quantity;
        private int period;
        private boolean subscription;
        private ItemSimpleResponseDto item;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        @Builder
        private SimpleResponse(long itemOrderId, int quantity, int period, boolean subscription,
            ItemSimpleResponseDto item, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
            this.itemOrderId = itemOrderId;
            this.quantity = quantity;
            this.period = period;
            this.subscription = subscription;
            this.item = item;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static SimpleResponse of(OrderItem orderItem) {
            return SimpleResponse.builder()
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubResponse { // 정기 구독 목록 조회

        private long orderId;
        private long itemOrderId;
        private int quantity;
        private int period;
        private ItemSimpleResponseDto item;
        private int totalPrice; // quantity * price
        private ZonedDateTime nextDelivery; // 다음 배송일
    }

}
