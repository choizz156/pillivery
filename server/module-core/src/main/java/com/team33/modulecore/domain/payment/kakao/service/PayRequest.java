package com.team33.modulecore.domain.payment.kakao.service;


import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto;

public interface PayRequest {

    KakaoResponseDto.Request requestOneTime(Order order);
    KakaoResponseDto.Request requestSubscription(Order order);
}
