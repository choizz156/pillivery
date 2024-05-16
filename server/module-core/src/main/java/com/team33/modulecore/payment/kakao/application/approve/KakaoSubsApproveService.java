package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.approve.SubscriptionApprove;
import com.team33.modulecore.payment.application.approve.SubscriptionApproveService;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsApproveService implements SubscriptionApproveService<KaKaoApproveResponse> {

	private final KakaoFirstSubsApprove kakaoFirstSubsApprove;
	private final SubscriptionApprove<KaKaoApproveResponse> subscriptionApprove;
	private final OrderService orderService;

	@Override
	public KaKaoApproveResponse approveFirstTime(Long orderId, ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest)approveRequest;

		KaKaoApproveResponse approve =
			kakaoFirstSubsApprove.approveFirstSubscription(request);

		orderService.changeOrderStatusToSubscribe(orderId, approve.getSid());

		// doKakaoScheduling(orderId);
		return approve;
	}

	@Override
	public KaKaoApproveResponse approveSubscribe(Order order) {
		return subscriptionApprove.approveSubscription(order);
	}
}
