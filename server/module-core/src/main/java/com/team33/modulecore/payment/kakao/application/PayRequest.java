package com.team33.modulecore.payment.kakao.application;


import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.payment.kakao.dto.KakaoResponseDto;

public interface PayRequest {

    KakaoResponseDto.Request requestOneTime(Order order);
    KakaoResponseDto.Request requestSubscription(Order order);
}
