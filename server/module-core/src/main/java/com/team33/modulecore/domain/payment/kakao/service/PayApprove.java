package com.team33.modulecore.domain.payment.kakao.service;


import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto;

public interface PayApprove {

    KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveFirstSubscription(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveSubscription(String sid, Order order);
}
