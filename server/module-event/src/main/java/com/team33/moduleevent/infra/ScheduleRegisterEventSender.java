package com.team33.moduleevent.infra;

import org.springframework.stereotype.Component;

import com.team33.moduleevent.application.EventSender;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ScheduleRegisterEventSender implements EventSender {

	private final RestTemplateSender restTemplateSender;

	@Override
	public void send(ApiEvent event) {
		String url = event.getUrl() + "/" + event.getParameters();
		restTemplateSender.sendToPost("scheduler register event", url, null, String.class);
	}
}
