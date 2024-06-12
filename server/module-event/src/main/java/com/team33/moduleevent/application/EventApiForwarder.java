package com.team33.moduleevent.application;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventApiForwarder {

	private final EventRepository eventsRepository;
	private final EventSender kakaoApiEventSender;
	private final EventSender scheduleRegisterEventSender;
	private final EventProcessor eventProcessor;

	@Scheduled(cron = "0 * * * * *")
	public void getAndSend() {
		List<ApiEvent> apiEvents = eventsRepository
			.findTop20ByStatusOrderByCreatedAt(EventStatus.READY);

		if (!apiEvents.isEmpty()) {
			sendEvents(apiEvents);
		}
	}

	private void sendEvents(List<ApiEvent> apiEvents) {
		for (ApiEvent apiEvent : apiEvents) {
			if (isScheduleRegistered(apiEvent)) {
				eventProcessor.register(apiEvent, scheduleRegisterEventSender);
				continue;
			}
			eventProcessor.register(apiEvent, kakaoApiEventSender);
		}
	}

	private boolean isScheduleRegistered(ApiEvent apiEvent) {
		return apiEvent.getType().equals(EventType.SCHEDULE_REGISTERED.name());
	}
}
