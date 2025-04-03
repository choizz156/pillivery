package com.team33.moduleevent.application;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.core.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;
import com.team33.moduleevent.handler.RefundEventHandler;

class RefundEventHandlerTest {

	@DisplayName("환불 이벤트를 저장할 수 있다.")
	@Test
	void onEventSet() {
		// given
		EventRepository eventRepository = mock(EventRepository.class);
		when(eventRepository.save(any(ApiEvent.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		RefundEventHandler refundEventHandler = new RefundEventHandler(eventRepository);

		// when
		refundEventHandler.onEventSet(new KakaoRefundedEvent("refundParams", "refundUrl"));

		// then
		verify(eventRepository, times(1)).save(any(ApiEvent.class));
	}
}