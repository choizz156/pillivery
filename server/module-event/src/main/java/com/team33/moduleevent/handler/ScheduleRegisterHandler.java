package com.team33.moduleevent.handler;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.payment.kakao.application.events.ScheduleRegisteredEvent;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ScheduleRegisterHandler {

	private static final String REGISTER_URL = "http://localhost:8080/api/schedules";
	private final EventRepository eventsRepository;

	@EventListener
	public void onEventSet(ScheduleRegisteredEvent apiEvent) {


		ApiEvent register = ApiEvent.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(String.valueOf(apiEvent.getParams()))
			.url(REGISTER_URL)
			.type(EventType.SCHEDULE_REGISTERED)
			.status(EventStatus.READY)
			.createdAt(LocalDateTime.now())
			.build();

		eventsRepository.save(register);
	}
}
