package com.team33.moduleevent.infra;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleevent.application.EventSender;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SchedulerPeriodChangedEventSender implements EventSender {

	private final RestTemplateSender restTemplateSender;

	@Override
	public void send(ApiEvent event) {
		Map<String, Object> params = getParamsMap(event);
		send(event, params);
	}

	private Map<String, Object> getParamsMap(ApiEvent event) {
		String[] parameters = event.getParameters().split(" ");
		String orderId = parameters[0];
		String itemOrderId = parameters[1];

		Map<String, Object> params = new ConcurrentHashMap<>();
		params.put("orderId", orderId);
		params.put("itemOrderId", itemOrderId);
		return params;
	}

	private void send(ApiEvent event, Map<String, Object> params) {
		try {
			restTemplateSender.sendToPost(params, event.getUrl(), null, String.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
