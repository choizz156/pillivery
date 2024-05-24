package com.team33.modulecore.payment.kakao.application.request;

import org.springframework.stereotype.Component;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.RequestFacade;
import com.team33.modulecore.payment.application.request.RequestService;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoRequestFacade implements RequestFacade<KakaoRequestResponse> {

	private final OrderFindHelper orderFindHelper;
	private final RequestService<KakaoRequestResponse> kakaoRequestService;
	private final RequestService<KakaoRequestResponse> kakaoSubsRequestService;

	@Override
	public KakaoRequestResponse request(long orderId) {
		Order order = orderFindHelper.findOrder(orderId);

		return order.isSubscription()
			? kakaoSubsRequestService.request(order)
			: kakaoRequestService.request(order);
	}
}
