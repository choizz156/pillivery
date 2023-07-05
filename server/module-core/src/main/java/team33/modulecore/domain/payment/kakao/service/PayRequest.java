package team33.modulecore.domain.payment.kakao.service;


import team33.modulecore.domain.order.entity.Order;
import team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto;

public interface PayRequest {

    KakaoResponseDto.Request requestOneTime(Order order);
    KakaoResponseDto.Request requestSubscription(Order order);
}
