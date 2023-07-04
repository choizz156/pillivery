package server.team33.domain.payment.kakao.service;

import server.team33.domain.payment.kakao.dto.KakaoResponseDto;

public interface PaymentTypeFacade {
    KakaoResponseDto.Request request(long orderId);

    KakaoResponseDto.Approve approve(String pgToken, Long orderId);

    KakaoResponseDto.Approve approveSubscription(Long orderId);
}
