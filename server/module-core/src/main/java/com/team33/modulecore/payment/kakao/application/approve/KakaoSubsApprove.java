package com.team33.modulecore.payment.kakao.application.approve;

import static com.team33.modulecore.payment.kakao.application.KakaoHeader.*;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.approve.SubscriptionApprove;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoSubsApprove
	implements SubscriptionApprove<KaKaoApproveResponse> {

	private final ParameterProvider parameterProvider;
	private final RestTemplate restTemplate;
	private static final String KAKAO_SUBSCRIPTION_URL = "https://kapi.kakao.com/v1/payment/subscription";

	@Override
	public KaKaoApproveResponse approveSubscription(Order order) {
		var subscriptionApproveParams = parameterProvider.getSubscriptionApproveParams(order);

		return getResponseDtoAboutApprove(subscriptionApproveParams);
	}

	private KaKaoApproveResponse getResponseDtoAboutApprove(
		MultiValueMap<String, String> params
	) {
		var entity = new HttpEntity<>(params, HTTP_HEADERS.getHeaders());

		return restTemplate.postForObject(
			KAKAO_SUBSCRIPTION_URL,
			entity,
			KaKaoApproveResponse.class
		);
	}
}
