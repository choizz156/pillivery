package com.team33.modulecore.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.kakao.application.ParameterProvider;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;

class KakaoSubsApproveTest {

	@DisplayName("정기 결제 시 승인 요청을 보낼 수 있다.")
	@Test
	void 정기_승인() throws Exception {
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
		KakaoSubsApprove kakaoSubsApprove =
			new KakaoSubsApprove(
				(params, url) -> new KakaoApproveResponse(),
				parameterProvider
			);

		//when
		KakaoApproveResponse kaKaoApproveResponse = kakaoSubsApprove.approveSubscription(order);

		//then
		assertThat(kaKaoApproveResponse).isNotNull();
	}

}