package com.team33.modulecore.core.payment.kakao.application.request;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.request.Request;
import com.team33.modulecore.core.payment.domain.request.RequestService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoRequestService implements RequestService<KakaoRequestResponse, Long> {

	private final Request<KakaoApiRequestResponse, Order> kakaoOneTimeRequestDispatcher;
	private final OrderFindHelper orderFindHelper;

	@Override
	public KakaoRequestResponse request(Long orderId) {

		Order order = orderFindHelper.findOrder(orderId);
		KakaoApiRequestResponse response = kakaoOneTimeRequestDispatcher.request(order);
		
		return KakaoResponseMapper.INSTANCE.toKakaoCoreRequestResponse(response);
	}
}
