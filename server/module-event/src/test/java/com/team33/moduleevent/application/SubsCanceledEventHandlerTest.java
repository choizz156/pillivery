package com.team33.moduleevent.application;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.payment.kakao.application.events.KakaoSubsCanceledEvent;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

class SubsCanceledEventHandlerTest {

	@DisplayName("정기 구독을 취소 이벤트를 저장할 수 있다.")
	@Test
	void onEventSet() {
		// given
		EventRepository eventRepository = mock(EventRepository.class);
		doNothing().when(eventRepository).save(any(ApiEvent.class));

		SubsCanceledEventHandler subsCanceledEventHandler = new SubsCanceledEventHandler(eventRepository);

		// when
		subsCanceledEventHandler.onEventSet(new KakaoSubsCanceledEvent("params", "refundUrl"));

		// then
		verify(eventRepository, times(1)).save(any(ApiEvent.class));
	}

}