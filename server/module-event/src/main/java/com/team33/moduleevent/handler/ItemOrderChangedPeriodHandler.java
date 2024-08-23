package com.team33.moduleevent.handler;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.team33.modulecore.core.order.events.ItemOrderChangedPeriod;
import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemOrderChangedPeriodHandler {

	private static final String HOST = "http://localhost:8080/api/schedules/update";
	private final EventRepository eventRepository;

	@TransactionalEventListener
	public void onEventSet(ItemOrderChangedPeriod apiEvent) {

		ApiEvent apiEventSet = ApiEvent.builder()
			.contentType("String")
			.parameters(apiEvent.getOrderId() + " " + apiEvent.getItemOrderId())
			.url(HOST)
			.type(EventType.SCHEDULE_CHANGED)
			.createdAt(LocalDateTime.now())
			.status(EventStatus.READY)
			.build();

		eventRepository.save(apiEventSet);
	}
}
