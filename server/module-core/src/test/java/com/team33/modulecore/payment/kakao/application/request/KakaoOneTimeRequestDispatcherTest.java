package com.team33.modulecore.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.order.domain.Price;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.request.KakaoOneTimeRequestDispatcher;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;


class KakaoOneTimeRequestDispatcherTest {

	@DisplayName("단건 결제 요청을 보낼 수 있다.")
	@Test
	void 단건_결제_요청() throws Exception {
		//given
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("orderPrice", new Price(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.set("sid", "sid")
			.sample();

		ParameterProvider parameterProvider = new ParameterProvider();

		KakaoOneTimeRequestDispatcher kakaoNormalRequest =
			new KakaoOneTimeRequestDispatcher(
				(params, url) -> new KakaoApiRequestResponse(),
				parameterProvider
			);

		//when
		KakaoApiRequestResponse kakaoApiRequestResponse = kakaoNormalRequest.request(order);

		//then
		assertThat(kakaoApiRequestResponse).isNotNull();
	}

}