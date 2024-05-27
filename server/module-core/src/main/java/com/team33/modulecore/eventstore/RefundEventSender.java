package com.team33.modulecore.eventstore;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRefundResponse;
import com.team33.moduleexternalapi.exception.PaymentApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RefundEventSender implements EventSender {

	private final PaymentClient<KakaoRefundResponse> kakaoRefundClient;
	private final ObjectMapper objectMapper;

	@Override
	public void send(ApiEventSet event) {
		try {

			Map<String, Object> parameters =
				objectMapper.readValue(event.getParameters(), new TypeReference<>() {});

			kakaoRefundClient.send(parameters, event.getUrl());

		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
