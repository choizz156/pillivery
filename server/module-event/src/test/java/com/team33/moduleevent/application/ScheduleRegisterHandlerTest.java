package com.team33.moduleevent.application;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.payment.kakao.application.events.ScheduleRegisteredEvent;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;
import com.team33.moduleevent.handler.ScheduleRegisterHandler;

class ScheduleRegisterHandlerTest {

	@DisplayName("스케쥴 등록 이벤트를 저장할 수 있다.")
	@Test
	void onEventSet() {
		// given
		EventRepository eventRepository = mock(EventRepository.class);
		doNothing().when(eventRepository).save(any(ApiEvent.class));

		ScheduleRegisterHandler scheduleRegisterHandler = new ScheduleRegisterHandler(eventRepository);

		// when
		scheduleRegisterHandler.onEventSet(new ScheduleRegisteredEvent(1));

		// then
		verify(eventRepository, times(1)).save(any(ApiEvent.class));
	}
}