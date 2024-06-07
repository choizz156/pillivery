package com.team33.modulecore.payment.kakao.application.refund;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.cancel.CancelSubscriptionService;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.application.events.KakaoSubsCanceledEvent;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsCancelService implements CancelSubscriptionService {

	private final static String CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive";

	private final ApplicationEventPublisher applicationEventPublisher;
	private final ParameterProvider parameterProvider;
	private final ObjectMapper objectMapper;

	@Override
	public void cancelSubscription(Order order) {

		String sid = checkSidNull(order);
		String params = mapToString(parameterProvider.getSubsCancelParams(sid));

		applicationEventPublisher.publishEvent(new KakaoSubsCanceledEvent(params, CANCEL_URL));
	}

	private String checkSidNull(Order order) {
		String sid = order.getSid();

		if (sid == null) {
			throw new NullPointerException("orderId = "+ order.getId() +"의 sid가 존재하지 않습니다.");
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
