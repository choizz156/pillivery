package com.team33.modulecore.eventstore.application;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.eventstore.domain.entity.ApiEventSet;
import com.team33.modulecore.eventstore.domain.repository.EventRepository;
import com.team33.modulecore.payment.kakao.application.events.KakaoRefundedEvent;

class RefundEventHandlerTest {

	@DisplayName("이벤트를 저장할 수 있다.")
	@Test
	void onEventSet() {
		// given
		EventRepository eventRepository = mock(EventRepository.class);
		doNothing().when(eventRepository).save(any(ApiEventSet.class));

		RefundEventHandler refundEventHandler = new RefundEventHandler(eventRepository);

		// when
		refundEventHandler.onEventSet(new KakaoRefundedEvent("refundParams", "refundUrl"));

		// then
		verify(eventRepository,times(1)).save(any(ApiEventSet.class));
	}
}