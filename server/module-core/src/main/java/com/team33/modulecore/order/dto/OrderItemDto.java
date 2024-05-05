package com.team33.modulecore.order.dto;

import com.team33.modulecore.item.dto.query.ItemSimpleResponseDto;
import java.time.ZonedDateTime;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
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
