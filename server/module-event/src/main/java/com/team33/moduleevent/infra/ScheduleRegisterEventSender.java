package com.team33.moduleevent.infra;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("orderId", List.of(event.getParameters()));

		ResponseEntity<Void> response = restTemplate.getForEntity(event.getUrl(), Void.class, params);

		if(!response.getStatusCode().is2xxSuccessful()) {
			throw new BusinessLogicException("schedule register error");
		}
	}
}
