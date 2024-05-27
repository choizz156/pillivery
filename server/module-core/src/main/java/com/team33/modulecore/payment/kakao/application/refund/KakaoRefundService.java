package com.team33.modulecore.payment.kakao.application.refund;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.payment.application.refund.RefundService;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoRefundService implements RefundService {

	private static final String REFUND_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";

	private final ApplicationEventPublisher applicationEventPublisher;
	private final ParameterProvider parameterProvider;
	private final OrderFindHelper orderFindHelper;
	private final ObjectMapper objectMapper;

	@Override
	public void refund(RefundContext refundContext) {

		String tid = orderFindHelper.findTid(refundContext.getOrderId());
		String params = mapToString(parameterProvider.getRefundParams(refundContext, tid));

		applicationEventPublisher.publishEvent(new KakaoRefundedEvent(params, REFUND_URL));
	}

	private String mapToString(Map<String, Object> refundParams) {
		try {
			return objectMapper.writeValueAsString(refundParams);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}

}
