package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.application.approve.SubscriptionApprove;
import com.team33.modulecore.core.payment.application.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.kakao.application.events.ScheduleRegisteredEvent;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsApproveService implements SubscriptionApproveService<KakaoApproveResponse> {

	private final ApplicationEventPublisher applicationEventPublisher;
	private final KakaoFirstSubsApprove kakaoFirstSubsApprove;
	private final SubscriptionApprove<KakaoApiApproveResponse> subscriptionApprove;

	@Override
	public KakaoApproveResponse approveFirstTime(ApproveRequest approveRequest) {

		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest)approveRequest;

		KakaoApiApproveResponse response = kakaoFirstSubsApprove.approveFirstSubscription(request);

		applicationEventPublisher.publishEvent(new ScheduleRegisteredEvent(request.getOrderId()));

		return KakaoResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}

	@Override
	public KakaoApproveResponse approveSubscribe(Order order) {
		KakaoApiApproveResponse response = subscriptionApprove.approveSubscription(order);
		return KakaoResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}
}
