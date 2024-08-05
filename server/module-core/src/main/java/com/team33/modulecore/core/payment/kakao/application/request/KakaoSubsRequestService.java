package com.team33.modulecore.core.payment.kakao.application.request;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.application.request.Request;
import com.team33.modulecore.core.payment.application.request.RequestService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsRequestService implements RequestService<KakaoRequestResponse> {

	private final Request<KakaoApiRequestResponse> kakaoSubsRequest;

	public KakaoRequestResponse request(Order order) {
		KakaoApiRequestResponse response = kakaoSubsRequest.request(order);
		return KakaoResponseMapper.INSTANCE.toKakaoCoreRequestResponse(response);
	}
}
