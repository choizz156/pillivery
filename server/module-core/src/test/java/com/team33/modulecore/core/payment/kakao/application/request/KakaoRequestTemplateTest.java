package com.team33.modulecore.core.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.moduleexternalapi.application.PaymentClient;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

class KakaoRequestTemplateTest {

	private PaymentClient<KakaoApiRequestResponse> kakaoRequestClient;
	private KakaoRequestTemplate<String> kakaoRequestTemplate;

	@BeforeEach
	void setUp() {

		kakaoRequestClient = mock(PaymentClient.class);

		kakaoRequestTemplate = spy(new KakaoRequestTemplate<String>(kakaoRequestClient) {
			@Override
			public Map<String, Object> getRequestParams(String requestObject) {
				return null;
			}
		});
	}

	@Test
	@DisplayName("템플릿 메서드 패턴을 통해 결제 요청을 처리할 수 있다")
	void test1() {
		// given
		String test = "test";

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(Params.CID.getValue(), "TC0ONETIME");
		requestParams.put(Params.PARTNER_ORDER_ID.getValue(), "123");

		KakaoApiRequestResponse expectedResponse = KakaoApiRequestResponse.builder()
			.tid("test_tid")
			.next_redirect_pc_url("url")
			.build();

		when(kakaoRequestTemplate.getRequestParams(test)).thenReturn(requestParams);

		when(kakaoRequestClient.send(eq(requestParams), eq("reqeustUrl")))
			.thenReturn(expectedResponse);

		// when
		KakaoApiRequestResponse response = kakaoRequestTemplate.request(test);

		// then
		verify(kakaoRequestTemplate, times(1)).getRequestParams(test);
		verify(kakaoRequestClient, times(1))
			.send(eq(requestParams), eq("reqeustUrl"));

		assertThat(response).isNotNull();
		assertThat(response.getTid()).isEqualTo("test_tid");
		assertThat(response.getNext_redirect_pc_url()).isEqualTo("url");
	}
}