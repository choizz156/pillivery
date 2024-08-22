package com.team33.moduleevent.infra;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

class ScheduleRegisterEventTest {

	@DisplayName("스케쥴 등록 api 요청을 보낼 수 있다.")
	@Test
	void send() {
		//given
		RestTemplateSender restTemplateSender = mock(RestTemplateSender.class);
		doNothing().when(restTemplateSender).sendToPost(anyString(), anyString(), eq(null), eq(String.class));

		ScheduleRegisterEventSender scheduleRegisterEventSender = new ScheduleRegisterEventSender(restTemplateSender);

		ApiEvent apiEvent = ApiEvent.builder()
			.parameters("1")
			.url("http://localhost:8080/api/schedules")
			.build();

		//when
		scheduleRegisterEventSender.send(apiEvent);

		//then
		verify(restTemplateSender, times(1)).sendToPost(anyString(), anyString(), eq(null),eq(String.class));
	}
}