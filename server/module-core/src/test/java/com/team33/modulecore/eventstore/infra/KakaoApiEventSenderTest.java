package com.team33.modulecore.eventstore.infra;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.eventstore.domain.entity.ApiEventSet;
import com.team33.moduleexternalapi.infra.KakaoCancelClient;

class KakaoApiEventSenderTest {

	@DisplayName("취소 요청을 위임할 수 있다.")
	@Test
	void send() {
		//given
		KakaoCancelClient kakaoCancelClient = mock(KakaoCancelClient.class);
		doNothing().when(kakaoCancelClient).send(anyString(), anyString());

		KakaoApiEventSender kakaoApiEventSender = new KakaoApiEventSender(kakaoCancelClient);

		ApiEventSet apiEventSet = ApiEventSet.builder()
			.parameters("parameters")
			.url("url")
			.build();

		//when
		kakaoApiEventSender.send(apiEventSet);

		//then
		verify(kakaoCancelClient, times(1)).send(anyString(), anyString());
	}
}