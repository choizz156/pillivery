package com.team33.moduleevent.infra;

import org.springframework.stereotype.Component;

import com.team33.moduleevent.application.EventSender;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.application.CancelClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApiEventSender implements EventSender {

	private final CancelClient kakaoCancelClient;

	@Override
	public void send(ApiEvent event) {
		kakaoCancelClient.send(event.getParameters(), event.getUrl());
	}
}
