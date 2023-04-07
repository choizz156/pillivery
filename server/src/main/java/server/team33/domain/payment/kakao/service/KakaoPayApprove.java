package server.team33.domain.payment.kakao.service;

import server.team33.domain.payment.kakao.dto.KakaoResponseDto;

public interface KakaoPayApprove {

    KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveSubscription(String tid, String pgToken, Long orderId);
}
