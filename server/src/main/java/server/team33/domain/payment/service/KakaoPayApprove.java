package server.team33.domain.payment.service;

import server.team33.domain.payment.dto.KakaoResponseDto;

public interface KakaoPayApprove {

    KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveSubscription(String tid, String pgToken, Long orderId);
}
