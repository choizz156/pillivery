package com.team33.modulecore.core.payment.kakao.application.request;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.request.Request;
import com.team33.modulecore.core.payment.domain.request.RequestService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubscriptionRequestService implements RequestService<KakaoRequestResponse, Long> {

	private final Request<KakaoApiRequestResponse, SubscriptionOrder> kakaoSubsRequest;
	private final SubscriptionOrderService subscriptionOrderService;

	@Override
	public KakaoRequestResponse request(Long subscriptionOrderId) {

		SubscriptionOrder subscriptionOrder = subscriptionOrderService.findById(subscriptionOrderId);
		KakaoApiRequestResponse response = kakaoSubsRequest.request(subscriptionOrder);

		return KakaoResponseMapper.INSTANCE.toKakaoCoreRequestResponse(response);
	}
}
