package com.team33.moduleevent.infra;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

class SchedulerPeriodChangedEventSenderTest {

	@DisplayName("스케쥴 주기 변경 요청을 위임할 수 있다.")
	@Test
	void send() throws Exception {
		RestTemplateSender restTemplateSender = mock(RestTemplateSender.class);
		when(restTemplateSender.sendToPost(anyMap(), anyString(), eq(null), eq(String.class)))
			.thenReturn("ok");

		SchedulerPeriodChangedEventSender schedulerPeriodChangedEventSender = new SchedulerPeriodChangedEventSender(
			restTemplateSender);

		ApiEvent apiEvent = ApiEvent.builder()
			.parameters("1 1")
			.url("http://localhost:8080/api/schedules")
			.build();

		//when
		schedulerPeriodChangedEventSender.send(apiEvent);

		//then
		verify(restTemplateSender, times(1)).sendToPost(anyMap(), anyString(), eq(null), eq(String.class));
	}

}