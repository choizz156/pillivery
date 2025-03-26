package com.team33.modulecore.core.payment.kakao.application.lookup;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.request.RequestService;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.dto.KakaoLookupResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiPayLookupResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoPayLookupService implements RequestService<KakaoLookupResponse> {

	private static final String LOOKUP_URL = "https://open-api.kakaopay.com/online/v1/payment/order";
	private final PaymentClient<KakaoApiPayLookupResponse> kakaoPayLookupClient;
	private final ParameterProvider parameterProvider;

	@Override
	public KakaoLookupResponse request(Order order) {
		Map<String, Object> lookupParams = parameterProvider.getLookupParams(order.getTid());

		KakaoApiPayLookupResponse response = kakaoPayLookupClient.send(lookupParams, LOOKUP_URL);

		return KakaoResponseMapper.INSTANCE.toKakaoCoreLookupResponse(response);
	}
}
