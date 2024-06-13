package com.team33.moduleevent.infra;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.moduleevent.application.EventSender;
import com.team33.moduleevent.domain.entity.ApiEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ScheduleRegisterEventSender implements EventSender {

	private final RestTemplate restTemplate;

	@Override
	public void send(ApiEvent event) {

		Map<String, Long> params = new ConcurrentHashMap<>();
		params.put("orderId", Long.valueOf(event.getParameters()));

		ResponseEntity<Void> response = restTemplate.postForEntity(event.getUrl(), null, Void.class, params);

		if(!response.getStatusCode().is2xxSuccessful()) {
			throw new BusinessLogicException("schedule register error");
		}
	}
}
