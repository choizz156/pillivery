package com.team33.moduleevent.infra;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiCancelResponse;
import com.team33.moduleexternalapi.infra.kakao.KakaoCancelClient;

class KakaoCancelEventSenderTest {

	@DisplayName("결제 취소 요청을 위임할 수 있다.")
	@Test
	void send() throws Exception{
		//given
		KakaoCancelClient kakaoCancelClient = mock(KakaoCancelClient.class);
		when(kakaoCancelClient.send(anyMap(), anyString())).thenReturn(new KakaoApiCancelResponse());

		KakaoCancelEventSender kakaoCancelEventSender = new KakaoCancelEventSender(kakaoCancelClient, new ObjectMapper());

		Map<String, Object> params = new HashMap<>();
		params.put("parameters", "prams");

		ObjectMapper objectMapper = new ObjectMapper();
		String param = objectMapper.writeValueAsString(params);

		ApiEvent apiEvent = ApiEvent.builder()
			.parameters(param)
			.url("url")
			.build();

		//when
		kakaoCancelEventSender.send(apiEvent);

		//then
		verify(kakaoCancelClient, times(1)).send(anyMap(), anyString());
	}
}