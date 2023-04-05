package server.team33.domain.payment.service;

import server.team33.domain.order.entity.Order;
import server.team33.domain.payment.dto.KakaoResponseDto;

public interface KakaoPayRequest {

    KakaoResponseDto.Request requestOneTime(Order order);
    KakaoResponseDto.Request requestSubscription(Order order);
}
