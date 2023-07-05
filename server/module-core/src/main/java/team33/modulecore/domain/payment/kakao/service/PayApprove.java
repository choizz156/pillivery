package team33.modulecore.domain.payment.kakao.service;


import team33.modulecore.domain.order.entity.Order;
import team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto;

public interface PayApprove {

    KakaoResponseDto.Approve approveOneTime(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveFirstSubscription(String tid, String pgToken, Long orderId);
    KakaoResponseDto.Approve approveSubscription(String sid, Order order);
}
