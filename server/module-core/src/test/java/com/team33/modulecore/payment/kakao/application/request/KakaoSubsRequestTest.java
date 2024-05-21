package com.team33.modulecore.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.dto.KakaoRequestResponse;

class KakaoSubsRequestTest {

	@DisplayName("정기 결제 요청을 보낼 수 있다.")
	@Test
	void 정기_결제_요청() throws Exception {

		//given
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("orderPrice", new OrderPrice(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.set("sid", "sid")
			.sample();

		ParameterProvider parameterProvider = new ParameterProvider();
		KakaoSubsRequest kakaoSubsRequest =
			new KakaoSubsRequest((params, url) -> new KakaoRequestResponse(), parameterProvider);

		//when
		KakaoRequestResponse kakaoRequestResponse = kakaoSubsRequest.requestSubscription(order);

		//then
		assertThat(kakaoRequestResponse).isNotNull();
	}

}