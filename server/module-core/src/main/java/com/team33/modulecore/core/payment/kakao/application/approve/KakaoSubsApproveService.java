package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApprove;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsApproveService implements SubscriptionApproveService<KakaoApproveResponse> {

	private final ApplicationEventPublisher applicationEventPublisher;
	private final KakaoFirstSubsApproveDispatcher kakaoFirstSubsApproveDispatcher;
	private final SubscriptionApprove<KakaoApiApproveResponse> subscriptionApprove;

	@Override
	public KakaoApproveResponse approveInitially(ApproveRequest approveRequest) {

		KakaoApproveRequest request = (KakaoApproveRequest)approveRequest;

		KakaoApiApproveResponse response = kakaoFirstSubsApproveDispatcher.approveFirstSubscription(request);

		applicationEventPublisher.publishEvent(new PaymentDateUpdatedEvent(request.getOrderId()));

		return KakaoResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}

	@Override
	public KakaoApproveResponse approveSubscribe(Order order) {
		KakaoApiApproveResponse response = subscriptionApprove.approveSubscription(order);

		applicationEventPublisher.publishEvent(new PaymentDateUpdatedEvent(order.getId()));

		return KakaoResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}
}
