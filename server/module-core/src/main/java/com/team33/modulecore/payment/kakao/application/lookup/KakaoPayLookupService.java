package com.team33.modulecore.payment.kakao.application.lookup;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.RequestService;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoPayLookupResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoPayLookupService implements RequestService<KakaoPayLookupResponse> {

	private static final String LOOKUP_URL = "https://open-api.kakaopay.com/online/v1/payment/order";
	private final PaymentClient<KakaoPayLookupResponse> kakaoPayLookupClient;
	private final ParameterProvider parameterProvider;

	@Override
	public KakaoPayLookupResponse request(Order order) {
		Map<String, Object> lookupParams = parameterProvider.getLookupParams(order.getTid());
		return kakaoPayLookupClient.send(lookupParams, LOOKUP_URL);
	}
}
