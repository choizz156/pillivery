package com.team33.modulecore.orderitem.dto;

import com.team33.modulecore.order.dto.OrderPostDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemServiceDto {

    private Long itemId;
    private boolean isSubscription;
    private int quantity;
    private int period;

    @Builder
    private OrderItemServiceDto(Long itemId, boolean isSubscription, int quantity, int period) {
        this.itemId = itemId;
        this.isSubscription = isSubscription;
        this.quantity = quantity;
        this.period = period;
    }

    public static OrderItemServiceDto to(OrderPostDto orderPostDto) {
        return OrderItemServiceDto.builder()
            .itemId(orderPostDto.getItemId())
            .isSubscription(orderPostDto.isSubscription())
            .quantity(orderPostDto.getQuantity())
            .period(orderPostDto.getPeriod())
            .build();
    }
}
