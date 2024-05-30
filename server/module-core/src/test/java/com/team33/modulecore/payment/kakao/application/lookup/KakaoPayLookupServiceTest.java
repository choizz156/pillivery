package com.team33.modulecore.payment.kakao.application.lookup;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.payment.kakao.dto.KakaoLookupResponse;
import com.team33.moduleexternalapi.dto.KakaoApiPayLookupResponse;

class KakaoPayLookupServiceTest {

	@DisplayName("주문 조회 요청을 위임 할 수 있다")
	@Test
	void 주문_조회_위임() throws Exception{
		//given
		ParameterProvider parameterProvider = new ParameterProvider();
		KakaoPayLookupService kakaoPayLookupService = new KakaoPayLookupService(
			((params, url) -> new KakaoApiPayLookupResponse()),
			parameterProvider
		);

		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("orderPrice", new OrderPrice(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.set("paymentCode.sid", "sid")
			.sample();

		//when
		KakaoLookupResponse response = kakaoPayLookupService.request(order);

		//then
		assertThat(response).isNotNull();
	}

}