package com.team33.modulecore.core.payment.kakao.application.request;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.request.Request;
import com.team33.modulecore.core.payment.domain.request.RequestService;
import com.team33.modulecore.core.payment.domain.request.RequestService1;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubscriptionRequestService implements RequestService1<KakaoRequestResponse> {

	private final Request<KakaoApiRequestResponse> kakaoSubsRequest;
	private final OrderFindHelper orderFindHelper;

	public KakaoRequestResponse request(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		KakaoApiRequestResponse response = kakaoSubsRequest.request(order);

		return KakaoResponseMapper.INSTANCE.toKakaoCoreRequestResponse(response);
	}
}
