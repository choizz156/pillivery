package com.team33.moduleevent.handler;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.team33.modulecore.core.payment.kakao.application.events.ScheduleRegisteredEvent;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleRegisterHandler {

	private static final String REGISETER_URL = "http://localhost:8080/schedules";
	private final EventRepository eventsRepository;

	@Async
	@EventListener
	public void onEventSet(ScheduleRegisteredEvent apiEvent) {

		ApiEvent refund = ApiEvent.builder()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.parameters(String.valueOf(apiEvent.getParams()))
			.url(REGISETER_URL)
			.type(EventType.SCHEDULE_REGISTERED)
			.status(EventStatus.READY)
			.createdAt(LocalDateTime.now())
			.build();

		eventsRepository.save(refund);
	}
}
