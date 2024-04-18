package com.team33.modulecore.payment.kakao.application;


import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.payment.kakao.dto.KakaoResponseDto;

public interface PayApprove {

    KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveFirstSubscription(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveSubscription(String sid, Order order);
}
