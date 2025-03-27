package com.team33.moduleevent.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.infra.KakaoCancelEventSender;
import com.team33.moduleevent.infra.ScheduleRegisterEventSender;
import com.team33.moduleexternalapi.config.RestTemplateErrorHandler;
import com.team33.moduleexternalapi.exception.PaymentApiException;

class EventDispatcherTest {

	@DisplayName("카카오 api 이벤트를 보낼 수 있다.")
	@Test
	void 카카오_api_이벤트_전송() throws Exception {
		//given
		FailEventService failEventService = mock(FailEventService.class);
		KakaoCancelEventSender kakaoCancelEventSender = mock(KakaoCancelEventSender.class);

		doNothing().when(kakaoCancelEventSender).send(any(ApiEvent.class));
		ApiEvent apiEvent = ApiEvent.builder()
			.status(EventStatus.READY)
			.build();

		EventDispatcher eventDispatcher = new EventDispatcher(failEventService);

		//when
		eventDispatcher.register(apiEvent, kakaoCancelEventSender);

		//then
		verify(kakaoCancelEventSender, times(1)).send(apiEvent);
		verify(failEventService, times(0)).saveFailEvent(any(ApiEvent.class), anyString());
		assertThat(apiEvent.getStatus()).isEqualByComparingTo(EventStatus.COMPLETE);
	}

	@DisplayName("스케쥴 등록 이벤트를 보낼 수 있다.")
	@Test
	void 스케쥴_등록_이벤트_전송() throws Exception {
		//given
		FailEventService failEventService = mock(FailEventService.class);
		ScheduleRegisterEventSender scheduleRegisterEventSender = mock(ScheduleRegisterEventSender.class);

		doNothing().when(scheduleRegisterEventSender).send(any(ApiEvent.class));

		ApiEvent apiEvent = ApiEvent.builder()
			.status(EventStatus.READY)
			.build();

		EventDispatcher eventDispatcher = new EventDispatcher(failEventService);

		//when
		eventDispatcher.register(apiEvent, scheduleRegisterEventSender);

		//then
		verify(scheduleRegisterEventSender, times(1)).send(apiEvent);
		verify(failEventService, times(0)).saveFailEvent(any(ApiEvent.class), anyString());
		assertThat(apiEvent.getStatus()).isEqualByComparingTo(EventStatus.COMPLETE);
	}

	@DisplayName("이벤트 전송이 두 번 실패했을 경우 이벤트 실패 저장소에 저장한다.")
	@Test
	void 이벤트_전송_실패1() throws Exception {
		//given
		FailEventService failEventService = mock(FailEventService.class);
		KakaoCancelEventSender kakaoCancelEventSender = mock(KakaoCancelEventSender.class);

		doThrow(new PaymentApiException("api error")).when(kakaoCancelEventSender).send(any(ApiEvent.class));

		ApiEvent apiEvent = ApiEvent.builder()
			.status(EventStatus.READY)
			.build();

		RestTemplateErrorHandler.ThreadLocalErrorMessage.set("api error");

		EventDispatcher eventDispatcher = new EventDispatcher(failEventService);

		//when
		eventDispatcher.register(apiEvent, kakaoCancelEventSender);

		//then
		verify(kakaoCancelEventSender, times(2)).send(apiEvent);
		verify(failEventService, times(1)).saveFailEvent(apiEvent, "api error");
		assertThat(apiEvent.getStatus()).isEqualByComparingTo(EventStatus.FAILED);
	}

	@DisplayName("이벤트 전송이 한 번 실패했을 경우 재전송한다.")
	@Test
	void 이벤트_재전송() throws Exception {
		//given
		FailEventService failEventService = mock(FailEventService.class);
		KakaoCancelEventSender kakaoCancelEventSender = mock(KakaoCancelEventSender.class);

		AtomicInteger atomicInteger = new AtomicInteger(0);

		doAnswer(invocation -> {
			if (atomicInteger.getAndIncrement() == 0) {
				throw new PaymentApiException("api error");
			}
			return null;
		})
			.when(kakaoCancelEventSender)
			.send(any(ApiEvent.class));

		ApiEvent apiEvent = ApiEvent.builder()
			.status(EventStatus.READY)
			.build();

		EventDispatcher eventDispatcher = new EventDispatcher(failEventService);

		//when
		eventDispatcher.register(apiEvent, kakaoCancelEventSender);

		//then
		verify(kakaoCancelEventSender, times(2)).send(apiEvent);
		verify(failEventService, times(0)).saveFailEvent(any(ApiEvent.class), anyString());
		assertThat(apiEvent.getStatus()).isEqualByComparingTo(EventStatus.COMPLETE);
	}
}