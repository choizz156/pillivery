package com.team33.moduleevent.infra;

import org.springframework.stereotype.Component;

import com.team33.moduleevent.application.EventSender;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduleRegisterEventSender implements EventSender {

	private static final String SCHEDULER_REGISTER_EVENT = "scheduler register event";
	private final RestTemplateSender restTemplateSender;

	@Override
	public void send(ApiEvent event) {
		String url = event.getUrl() + "/" + event.getParameters();

		restTemplateSender.sendToPost(SCHEDULER_REGISTER_EVENT, url, null, String.class);
	}
}
