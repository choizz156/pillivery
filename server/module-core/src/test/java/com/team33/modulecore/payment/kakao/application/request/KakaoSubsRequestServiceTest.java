package com.team33.modulecore.payment.kakao.application.request;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.order.domain.OrderPrice;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.kakao.application.request.KakaoSubsRequestService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

class KakaoSubsRequestServiceTest {


	@DisplayName("정기 결제 요청을 위임할 수 있다.")
	@Test
	void 정기_결제_요청_위임() throws Exception{

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

		//given
		KakaoSubsRequestService kakaoSubsRequestService = new KakaoSubsRequestService(o -> new KakaoApiRequestResponse());

		//when
		KakaoRequestResponse kakaoApiRequestResponse = kakaoSubsRequestService.request(order);

		//then
		assertThat(kakaoApiRequestResponse).isNotNull();
	}
}