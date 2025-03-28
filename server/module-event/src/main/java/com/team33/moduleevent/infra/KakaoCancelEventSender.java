package com.team33.moduleevent.infra;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleevent.application.EventSender;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.kakao.KakaoCancelClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoCancelEventSender implements EventSender {

	private final KakaoCancelClient kakaoCancelClient;
	private final ObjectMapper objectMapper;

	@Override
	public void send(ApiEvent event) {

		Map<String, Object> params = extracted(event.getParameters());

		kakaoCancelClient.send(params, event.getUrl());
	}

	private Map<String, Object> extracted(String params) {

		try {
			return objectMapper.readValue(params, new TypeReference<>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
