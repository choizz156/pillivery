package com.team33.modulecore.eventstore.infra;

import org.springframework.stereotype.Component;

import com.team33.modulecore.eventstore.api.EventSender;
import com.team33.modulecore.eventstore.domain.entity.ApiEventSet;
import com.team33.moduleexternalapi.application.CancelClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApiEventSender implements EventSender {

	private final CancelClient kakaoRefundClient;

	@Override
	public void send(ApiEventSet event) {
			kakaoRefundClient.send(event.getParameters(), event.getUrl());
	}
}
