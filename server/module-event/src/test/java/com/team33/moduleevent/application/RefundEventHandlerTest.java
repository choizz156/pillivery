package com.team33.moduleevent.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.team33.modulecore.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

class RefundEventHandlerTest {

	@DisplayName("환불 이벤트를 저장할 수 있다.")
	@Test
	void onEventSet() {
		// given
		EventRepository eventRepository = Mockito.mock(EventRepository.class);
		Mockito.doNothing().when(eventRepository).save(ArgumentMatchers.any(ApiEvent.class));

		RefundEventHandler refundEventHandler = new RefundEventHandler(eventRepository);

		// when
		refundEventHandler.onEventSet(new KakaoRefundedEvent("refundParams", "refundUrl"));

		// then
		Mockito.verify(eventRepository, Mockito.times(1)).save(ArgumentMatchers.any(ApiEvent.class));
	}
}