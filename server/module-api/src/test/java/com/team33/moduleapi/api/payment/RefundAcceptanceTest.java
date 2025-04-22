package com.team33.moduleapi.api.payment;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.payment.dto.RefundDto;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.payment.domain.refund.RefundService;
import com.team33.modulecore.core.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.moduleevent.handler.RefundEventHandler;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;

@ExtendWith(MockitoExtension.class)
class RefundAcceptanceTest extends ApiTest {

	private Order order;

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	@Autowired
	private OrderStatusService orderStatusService;

	@Autowired
	private RefundService refundService;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@MockBean
	private RefundEventHandler refundEventHandler;


	@DisplayName("결제를 취소할 수 있다.")
	@Test
	void 결제_취소() throws Exception {
		order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.set("orderPrice.totalPrice", 1000)
			.sample();

		orderCommandRepository.save(order);

		//given
		MockMvcRequestSpecification given = RestAssuredMockMvc.given()
			.mockMvc(standaloneSetup(
					new RefundController(
						paymentMapper,
						refundService,
						orderStatusService
					)
				)
					.build()
			)
			.log().all();

		RefundDto refundDto = RefundDto.builder()
			.cancelAmount(1000)
			.cancelTaxFreeAmount(0)
			.build();

		//when
		//@formatter:off
		given
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(refundDto)
		.when()
			.post("/api/payments/refund/{orderId}", 1L)
		.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body(containsString("complete"));
		//@formatter:on
	}

	@DisplayName("결제 취소 이벤트가 발행되면, 이벤트 구독자가 동작한다.")
	@Test
	void 환불_취소_이벤트_발행() throws Exception{
		//given
		KakaoRefundedEvent kakaoRefundedEvent = new KakaoRefundedEvent("params", "url");
		//when
		applicationEventPublisher.publishEvent(kakaoRefundedEvent);

		//then
		verify(refundEventHandler, times(1)).onEventSet(kakaoRefundedEvent);
	}
}