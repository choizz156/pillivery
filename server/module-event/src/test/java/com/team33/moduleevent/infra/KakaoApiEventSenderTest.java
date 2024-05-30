package com.team33.moduleevent.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.team33.moduleevent.domain.entity.ApiEventSet;
import com.team33.moduleexternalapi.infra.KakaoCancelClient;

class KakaoApiEventSenderTest {

	@DisplayName("취소 요청을 위임할 수 있다.")
	@Test
	void send() {
		//given
		KakaoCancelClient kakaoCancelClient = Mockito.mock(KakaoCancelClient.class);
		Mockito.doNothing().when(kakaoCancelClient).send(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

		KakaoApiEventSender kakaoApiEventSender = new KakaoApiEventSender(kakaoCancelClient);

		ApiEventSet apiEventSet = ApiEventSet.builder()
			.parameters("parameters")
			.url("url")
			.build();

		//when
		kakaoApiEventSender.send(apiEventSet);

		//then
		Mockito.verify(kakaoCancelClient, Mockito.times(1)).send(
			ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}
}