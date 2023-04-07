package server.team33.domain.payment.kakao.service;

import server.team33.domain.order.entity.Order;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto;

public interface KakaoPayRequest {

    KakaoResponseDto.Request requestOneTime(Order order);
    KakaoResponseDto.Request requestSubscription(Order order);
}
