package com.team33.moduleevent.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleevent.application.EventSender;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.WebClientSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionRegisterEventSender implements EventSender {

	private final WebClientSender webClientSender;

	@Override
	public void send(ApiEvent event) {
		try {
			webClientSender.sendToPostSync(Map.of(), event.getUrl() + event.getParameters(), null, String.class);
		} catch (JsonProcessingException e) {
			throw new PaymentApiException(e.getMessage());
		}
	}
}
