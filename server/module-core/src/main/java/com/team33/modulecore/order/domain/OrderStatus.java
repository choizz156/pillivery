package com.team33.modulecore.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    REQUEST(1, "주문 요청"),
    COMPLETE(2, "주문 완료"),
    CANCEL(3, "주문 취소"),
    SUBSCRIBE(4,"구독 중");

    private final int step;
    private final String stepDescription;
}
