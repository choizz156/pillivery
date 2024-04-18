package com.team33.modulecore.payment.kakao.application;


import com.team33.modulecore.payment.kakao.dto.KakaoResponseDto;

public interface PaymentFacade {
    KakaoResponseDto.Request request(long orderId);

    KakaoResponseDto.Approve approve(String pgToken, Long orderId);

    KakaoResponseDto.Approve approveSubscription(Long orderId);
}
