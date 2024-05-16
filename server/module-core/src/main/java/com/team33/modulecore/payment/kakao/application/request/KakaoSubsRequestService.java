package com.team33.modulecore.payment.kakao.application.request;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.SubscriptionRequest;
import com.team33.modulecore.payment.application.request.SubscriptionRequestService;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsRequestService implements SubscriptionRequestService<KakaoRequestResponse> {

	private final SubscriptionRequest<KakaoRequestResponse> subscriptionRequest;

	public KakaoRequestResponse requestSubscription(Order order) {
		return subscriptionRequest.requestSubscription(order);
	}
}
