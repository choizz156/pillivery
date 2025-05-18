package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApprove;
import com.team33.modulecore.core.payment.domain.approve.SubscriptionApproveService;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;
import com.team33.modulecore.core.payment.kakao.application.events.SubscriptionFailedEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.exception.SubscriptionPaymentException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubscriptionApproveService implements SubscriptionApproveService<KakaoApproveResponse, SubscriptionOrder> {

	private final ApplicationEventPublisher applicationEventPublisher;
	private final KakaoFirstSubsApproveDispatcher kakaoFirstSubsApproveDispatcher;
	private final SubscriptionApprove<KakaoApiApproveResponse, SubscriptionOrder> kakaoSubscriptionApproveDispatcher;

	@Override
	public KakaoApproveResponse approveInitially(ApproveRequest approveRequest) {

		KakaoApproveRequest request = (KakaoApproveRequest)approveRequest;

		KakaoApiApproveResponse response = kakaoFirstSubsApproveDispatcher.approveFirstSubscription(request);

		applicationEventPublisher.publishEvent(new PaymentDateUpdatedEvent(request.getSubscriptionOrderId()));

		return KakaoResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}

	@Override
	public KakaoApproveResponse approveSubscription(SubscriptionOrder subscriptionOrder) {

		KakaoApiApproveResponse response;

		try {
			response = kakaoSubscriptionApproveDispatcher.approveSubscription(subscriptionOrder);

		} catch (SubscriptionPaymentException e) {
			applicationEventPublisher.publishEvent(new SubscriptionFailedEvent(subscriptionOrder.getId()));
			throw e;
		}
		return KakaoResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}
}
