package com.team33.moduleevent.infra;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.kakao.KakaoCancelClient;

class KakaoCancelEventSenderTest {

	@DisplayName("결제 취소 요청을 위임할 수 있다.")
	@Test
	void send() {
		//given
		KakaoCancelClient kakaoCancelClient = mock(KakaoCancelClient.class);
		doNothing().when(kakaoCancelClient).send(anyMap(), anyString());

		KakaoCancelEventSender kakaoCancelEventSender = new KakaoCancelEventSender(kakaoCancelClient, new ObjectMapper());

		ApiEvent apiEvent = ApiEvent.builder()
			.parameters("parameters")
			.url("url")
			.build();

		//when
		kakaoCancelEventSender.send(apiEvent);

		//then
		verify(kakaoCancelClient, times(1)).send(anyMap(), anyString());
	}
}