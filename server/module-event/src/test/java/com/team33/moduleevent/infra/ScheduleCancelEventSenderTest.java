package com.team33.moduleevent.infra;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

class ScheduleCancelEventSenderTest {

	@DisplayName("스케쥴 취소 요청을 위임할 수 있다.")
	@Test
	void send() {
		RestTemplateSender restTemplateSender = mock(RestTemplateSender.class);
		doNothing().when(restTemplateSender).sendToPost(anyString(), anyString(), eq(null), eq(String.class));

		ScheduleCancelEventSender scheduleCancelEventSender = new ScheduleCancelEventSender(restTemplateSender);

		ApiEvent apiEvent = ApiEvent.builder()
			.parameters("1")
			.url("http://localhost:8080/api/schedules")
			.build();

		//when
		scheduleCancelEventSender.send(apiEvent);

		//then
		verify(restTemplateSender, times(1)).sendToPost(anyString(), anyString(), eq(null),eq(String.class));
	}
}