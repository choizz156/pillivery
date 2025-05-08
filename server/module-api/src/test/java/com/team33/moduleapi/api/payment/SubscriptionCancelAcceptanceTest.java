package com.team33.moduleapi.api.payment;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.exception.controller.ExceptionController;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.SubscriptionOrderRepository;
import com.team33.modulecore.core.payment.kakao.application.events.KakaoSubsCanceledEvent;
import com.team33.moduleevent.handler.SubscriptionCanceledEventHandler;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;

@ExtendWith(MockitoExtension.class)
class SubscriptionCancelAcceptanceTest extends ApiTest {

	@Autowired
	private OrderStatusService orderStatusService;

	@Autowired
	private SubscriptionOrderRepository subscriptionOrderRepository;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@MockBean
	private SubscriptionCanceledEventHandler subscriptionEventHandler;

	private MockMvcRequestSpecification given;
	private SubscriptionOrder subscriptionOrder;

	@BeforeEach
	void setUp() {

		subscriptionOrder = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionOrder.class)
			.setNull("id")
			.setNull("orderItem")
			.set("orderCommonInfo.paymentToken.sid", "sid")
			.sample();

		given = RestAssuredMockMvc.given()
			.mockMvc(standaloneSetup(
					new SubscriptionCancelController(
						orderStatusService
					)
				)
					.setControllerAdvice(new ExceptionController())
					.build()
			)
			.log().all();
	}

	@DisplayName("정기 결제를 취소할 수 있다.")
	@Test
	void 정기_결제_취소() throws Exception {
		//given
		subscriptionOrderRepository.save(subscriptionOrder);

		//when then
		//@formatter:off
		given
		.when()
			.post("/api/payments/subscriptions/cancel/{orderId}", 1)
		.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body(containsString("complete"));
		//@formatter:on
	}

	@DisplayName("sid가 null일 경우 예외를 던진다.")
	@Test
	void 정기_결제_취소_예외() throws Exception {
		//given
		subscriptionOrder.addSid(null);
		subscriptionOrderRepository.save(subscriptionOrder);

		//@formatter:off
		given
		.when()
			.post("/api/payments/subscriptions/cancel/{orderId}", 1)
		.then()
			.log().all()
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.body(containsString("알 수 없는 오류가 발생했습니다."));
		//@formatter:on
	}

	@DisplayName("정기 결제 취소 이벤트를 발행하면, 이벤트 구독자가 동작한다.")
	@Test
	void 정기_결제_취소_이벤트_발행() throws Exception {
		//given
		KakaoSubsCanceledEvent kakaoSubsCanceledEvent = new KakaoSubsCanceledEvent("sid", "url");

		//when
		applicationEventPublisher.publishEvent(kakaoSubsCanceledEvent);

		//then
		verify(subscriptionEventHandler, times(1)).onEventSet(kakaoSubsCanceledEvent);
	}
}