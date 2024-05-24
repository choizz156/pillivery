package com.team33.modulecore.payment.kakao.application.request;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.Request;
import com.team33.modulecore.payment.application.request.RequestService;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoRequestService implements RequestService<KakaoRequestResponse> {

	private final Request<KakaoRequestResponse> kakaoOneTimeRequest;

	public KakaoRequestResponse request(Order order) {
		return kakaoOneTimeRequest.request(order);
	}
}
