package com.team33.moduleevent.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.infra.KakaoCancelEventSender;
import com.team33.moduleexternalapi.config.RestTemplateErrorHandler;
import com.team33.moduleexternalapi.exception.PaymentApiException;

class EventDispatcherTest {

	private FailEventService failEventService;
	private KakaoCancelEventSender kakaoCancelEventSender;
	private EventDispatcher eventDispatcher;
	private ApiEvent apiEvent;

	@BeforeEach
	void setUp() {
		failEventService = mock(FailEventService.class);
		kakaoCancelEventSender = mock(KakaoCancelEventSender.class);
		eventDispatcher = new EventDispatcher(failEventService);
		
		apiEvent = ApiEvent.builder()
			.status(EventStatus.READY)
			.build();
	}

	@Test
	@DisplayName("이벤트 전송 성공시 상태가 COMPLETE로 변경된다")
	void test1() {
		// given
		doNothing().when(kakaoCancelEventSender).send(any(ApiEvent.class));

		// when
		eventDispatcher.register(apiEvent, kakaoCancelEventSender);

		// then
		verify(kakaoCancelEventSender, times(1)).send(apiEvent);
		verify(failEventService, never()).saveFailEvent(any(ApiEvent.class), anyString());
		assertThat(apiEvent.getStatus()).isEqualTo(EventStatus.COMPLETE);
	}

	@Test
	@DisplayName("이벤트 전송이 두 번 실패하면 상태가 FAILED로 변경되고 실패 이벤트로 저장된다")
	void test2() {
		// given
		doThrow(new PaymentApiException("api error"))
			.when(kakaoCancelEventSender)
			.send(any(ApiEvent.class));

		RestTemplateErrorHandler.ThreadLocalErrorMessage.set("api error");

		// when
		eventDispatcher.register(apiEvent, kakaoCancelEventSender);

		// then
		verify(kakaoCancelEventSender, times(2)).send(apiEvent);
		verify(failEventService, times(1)).saveFailEvent(apiEvent, "api error");
		assertThat(apiEvent.getStatus()).isEqualTo(EventStatus.FAILED);
	}

	@Test
	@DisplayName("이벤트 전송이 한 번 실패하고 재시도 시 성공하면 상태가 COMPLETE로 변경된다")
	void test3() {
		// given
		AtomicInteger attemptCount = new AtomicInteger(0);

		doAnswer(invocation -> {
			if (attemptCount.getAndIncrement() == 0) {
				throw new PaymentApiException("api error");
			}
			return null;
		})
			.when(kakaoCancelEventSender)
			.send(any(ApiEvent.class));

		// when
		eventDispatcher.register(apiEvent, kakaoCancelEventSender);

		// then
		verify(kakaoCancelEventSender, times(2)).send(apiEvent);
		verify(failEventService, never()).saveFailEvent(any(ApiEvent.class), anyString());
		assertThat(apiEvent.getStatus()).isEqualTo(EventStatus.COMPLETE);
	}
}