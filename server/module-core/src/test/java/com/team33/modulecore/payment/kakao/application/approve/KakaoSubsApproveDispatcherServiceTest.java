package com.team33.modulecore.payment.kakao.application.approve;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.order.domain.Price;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.application.ParameterProvider;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoFirstSubsApproveDispatcher;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoSubsApproveDispatcher;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoSubsApproveService;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

class KakaoSubsApproveDispatcherServiceTest {

	@DisplayName("첫 정기 결제 승인을 위임할 수 있다.")
	@Test
	void 첫_정기_승인_위임() throws Exception {
		//given
		KakaoSubsApproveService kakaoSubsApproveService = getKakaoSubsApproveService(
			applicationEventPublisher -> {
			});

		ApproveRequest request = KakaoApproveRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		KakaoApproveResponse kaKaoApiApproveResponse = kakaoSubsApproveService.approveInitially(request);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

	@DisplayName("기존 정기 결제 승인을 위임할 수 있다.")
	@Test
	void 정기_승인_위임() throws Exception {

		//given
		KakaoSubsApproveService kakaoSubsApproveService = getKakaoSubsApproveService(
			applicationEventPublisher -> {
			});

		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("orderPrice", new Price(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.set("paymentId.sid", "sid")
			.set("orderItems", null)
			.set("user", null)
			.sample();

		//when
		KakaoApproveResponse kaKaoApiApproveResponse = kakaoSubsApproveService.approveSubscribe(order);

		//then
		assertThat(kaKaoApiApproveResponse).isNotNull();
	}

	@DisplayName("첫 정기 결제 승인 후 결제일을 업데이트 이벤트를 발행한다.")
	@Test
	void test2() throws Exception {
		//given
		ApplicationEventPublisher applicationContext = getApplicationEventPublisher();

		KakaoSubsApproveService kakaoSubsApproveService = getKakaoSubsApproveService(applicationContext);

		ApproveRequest request = KakaoApproveRequest.builder()
			.orderId(1L)
			.pgtoken("pgToken")
			.tid("tid")
			.build();

		//when
		kakaoSubsApproveService.approveInitially(request);

		//then
		verify(applicationContext, times(1)).publishEvent(any(PaymentDateUpdatedEvent.class));

	}

	@DisplayName("기존 정기 결제 승인시 결제 날짜 업데이트 이벤트를 발행한다.")
	@Test
	void test3() throws Exception {

		//given
		ApplicationEventPublisher applicationContext = getApplicationEventPublisher();

		KakaoSubsApproveService kakaoSubsApproveService = getKakaoSubsApproveService(applicationContext);

		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("mainItemName", "test")
			.set("totalItemsCount", 3)
			.set("totalQuantity", 3)
			.set("orderPrice", new Price(3000, 200))
			.set("orderItems", List.of())
			.set("receiver", null)
			.set("paymentId.sid", "sid")
			.set("orderItems", null)
			.set("user", null)
			.sample();

		//when
		KakaoApproveResponse kaKaoApiApproveResponse = kakaoSubsApproveService.approveSubscribe(order);

		//then
		verify(applicationContext, times(1)).publishEvent(any(PaymentDateUpdatedEvent.class));
	}

	private KakaoSubsApproveService getKakaoSubsApproveService(ApplicationEventPublisher applicationContext) {
		ParameterProvider parameterProvider = new ParameterProvider();

		KakaoSubsApproveService kakaoSubsApproveService = new KakaoSubsApproveService(
			applicationContext,
			new KakaoFirstSubsApproveDispatcher((params, url) -> new KakaoApiApproveResponse(), parameterProvider),
			new KakaoSubsApproveDispatcher((params, url) -> new KakaoApiApproveResponse(), parameterProvider)
		);
		return kakaoSubsApproveService;
	}

	private ApplicationEventPublisher getApplicationEventPublisher() {
		ApplicationEventPublisher applicationContext = mock(ApplicationEventPublisher.class);
		doNothing().when(applicationContext).publishEvent(any(PaymentDateUpdatedEvent.class));
		return applicationContext;
	}

}