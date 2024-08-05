package com.team33.modulecore.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.order.domain.OrderPrice;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoFirstSubsApprove;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoSubsApprove;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoSubsApproveService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoSubsApproveServiceTest {

	@DisplayName("첫 정기 결제 승인을 위임할 수 있다.")
	@Test
	void 첫_정기_승인_위임() throws Exception{
		//given
		ParameterProvider parameterProvider = new ParameterProvider();

		KakaoSubsApproveService kakaoSubsApproveService = new KakaoSubsApproveService(
			applicationEventPublisher -> {},
			new KakaoFirstSubsApprove((params, url) -> new KakaoApiApproveResponse(), parameterProvider),
			new KakaoSubsApprove((params, url) -> new KakaoApiApproveResponse(), parameterProvider)
		);

		ApproveRequest request = KakaoApproveOneTimeRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		KakaoApproveResponse kaKaoApiApproveResponse = kakaoSubsApproveService.approveFirstTime(request);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

	@DisplayName("기존 정기 결제 승인을 위임할 수 있다.")
	@Test
	void 정기_승인_위임() throws Exception{

		//given
		ParameterProvider parameterProvider = new ParameterProvider();

		KakaoSubsApproveService kakaoSubsApproveService = new KakaoSubsApproveService(
			applicationEventPublisher -> {},
			new KakaoFirstSubsApprove((params, url) -> new KakaoApiApproveResponse(), parameterProvider),
			new KakaoSubsApprove((params, url) -> new KakaoApiApproveResponse(), parameterProvider)
		);

		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("orderPrice", new OrderPrice(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.set("sid", "sid")
			.set("orderItems", null)
			.set("user", null)
			.sample();

		//when
		KakaoApproveResponse kaKaoApiApproveResponse = kakaoSubsApproveService.approveSubscribe(order);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

}