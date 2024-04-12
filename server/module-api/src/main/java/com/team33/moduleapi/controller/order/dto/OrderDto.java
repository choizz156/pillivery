package com.team33.moduleapi.controller.order.dto;


import com.team33.modulecore.domain.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.domain.order.entity.OrderStatus;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class OrderDto {

    @Setter
    public static class Patch { // 이름, 주소, 번호를 변경하는 경우
        private long orderId;
        private String name;
        private String address;
        private String detailAddress;
        private String phone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleResponse { // 주문 목록 조회

        private long orderId;
        private OrderStatus orderStatus;
        private int totalItems;
        private int expectPrice;
        private boolean subscription;
        private ItemSimpleResponseDto item;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
    }

}
