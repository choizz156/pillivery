package server.team33.domain.payment.kakao.service;

import server.team33.domain.order.entity.Order;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto;

public interface PayApprove {

    KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveFirstSubscription(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveSubscription(String sid, Order order);
}
