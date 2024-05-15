package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.payment.application.approve.SubscriptionApproveService;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsApproveService implements SubscriptionApproveService<KaKaoApproveResponse> {

	private final KakaoSubsFirstApprove kakaoSubsFirstApprove;
	private final OrderService orderService;

	@Override
	public KaKaoApproveResponse approveFirstTime(Long orderId, ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;

		KaKaoApproveResponse approve =
			kakaoSubsFirstApprove.approveFirstSubscription(request);

		orderService.addSid(orderId, approve.getSid());
		orderService.changeOrderStatusToSubscribe(orderId);
		// doKakaoScheduling(orderId);
		return approve;
	}
}
