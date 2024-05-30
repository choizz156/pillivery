package com.team33.modulecore.eventstore.infra;

import org.springframework.stereotype.Component;

import com.team33.modulecore.eventstore.api.EventSender;
import com.team33.modulecore.eventstore.domain.entity.ApiEventSet;
import com.team33.moduleexternalapi.application.CancelClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApiEventSender implements EventSender {

	private final CancelClient kakaoCancelClient;

	@Override
	public void send(ApiEventSet event) {
		kakaoCancelClient.send(event.getParameters(), event.getUrl());
	}
}
