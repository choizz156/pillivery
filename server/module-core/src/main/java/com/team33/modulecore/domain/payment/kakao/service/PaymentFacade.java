package com.team33.modulecore.domain.payment.kakao.service;


import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto;

public interface PaymentFacade {
    KakaoResponseDto.Request request(long orderId);

    KakaoResponseDto.Approve approve(String pgToken, Long orderId);

    KakaoResponseDto.Approve approveSubscription(Long orderId);
}
