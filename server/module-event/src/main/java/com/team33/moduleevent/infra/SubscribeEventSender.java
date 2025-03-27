package com.team33.moduleevent.infra;

import org.springframework.stereotype.Component;

import com.team33.moduleevent.application.EventSender;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscribeEventSender implements EventSender {

	private final RestTemplateSender restTemplateSender;

	@Override
	public void send(ApiEvent event) {
		restTemplateSender.sendToPost("", event.getUrl() + event.getParameters(), null, String.class);
	}
}
