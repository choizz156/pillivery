package com.team33.modulecore.core.payment.kakao.application.refund;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.cancel.SubscriptionCancelService;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.events.KakaoSubscriptionCanceledEvent;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubscriptionCancelService implements SubscriptionCancelService<SubscriptionOrder> {

	private final static String CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive";

	private final ApplicationEventPublisher applicationEventPublisher;
	private final ParameterProvider parameterProvider;
	private final ObjectMapper objectMapper;

	@Override
	public void cancelSubscription(SubscriptionOrder subscriptionOrder) {

		String sid = checkSidNull(subscriptionOrder);
		String params = mapToString(parameterProvider.getSubsCancelParams(sid));

		applicationEventPublisher.publishEvent(new KakaoSubscriptionCanceledEvent(params, CANCEL_URL));
	}

	private String checkSidNull(SubscriptionOrder subscriptionOrder) {
		String sid = subscriptionOrder.getSid();

		if (sid == null) {
			throw new NullPointerException("orderId = "+ subscriptionOrder.getId() +"의 sid가 존재하지 않습니다.");
		}

		return sid;
	}

	private String mapToString(Map<String, Object> cancelParam) {
		try {
			return objectMapper.writeValueAsString(cancelParam);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
