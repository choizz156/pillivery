package com.team33.moduleevent.handler;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.team33.modulecore.payment.kakao.application.refund.SchedulerCanceledEvent;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SchedulerCanceledHandler {

	private static final String SCHEDULES_CANCEL_URL = "http://localhost:8080/schedules/cancel";
	private final EventRepository eventsRepository;

	@Async
	@EventListener
	public void onEventSet(SchedulerCanceledEvent apiEvent) {

		ApiEvent event = ApiEvent.builder()
			.contentType("String")
			.parameters(String.valueOf(apiEvent.getOrderId()))
			.url(SCHEDULES_CANCEL_URL)
			.type(EventType.SCHEDULE_CANCELED.name())
			.status(EventStatus.READY)
			.build();

		eventsRepository.save(event);
	}
}
