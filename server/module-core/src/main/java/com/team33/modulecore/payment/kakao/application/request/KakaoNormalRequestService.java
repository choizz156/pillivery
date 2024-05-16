package com.team33.modulecore.payment.kakao.application.request;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.NormalRequest;
import com.team33.modulecore.payment.application.request.NormalRequestService;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoNormalRequestService implements NormalRequestService<KakaoRequestResponse> {

	private final NormalRequest<KakaoRequestResponse> normalRequest;

	public KakaoRequestResponse requestOneTime(Order order) {
		return normalRequest.requestOneTime(order);
	}
}
