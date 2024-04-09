package com.team33.modulecore.domain.order.dto;


import com.team33.modulecore.domain.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.domain.order.entity.OrderStatus;
import java.time.ZonedDateTime;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class OrderDto {
    @Getter
    public static class Post {
        private long itemId;
        private String realName;
        private String phoneNumber;
        @Min(value = 1, message = "수량은 1개 이상 선택해주세요.")
        private int quantity;
        private int period;
        private boolean subscription;
        private String address;
        private String detailAddress;
    }

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
