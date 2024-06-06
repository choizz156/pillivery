package com.team33.moduleapi.ui.payment;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.ui.payment.dto.RefundDto;
import com.team33.moduleapi.ui.payment.mapper.PaymentMapper;
import com.team33.modulecore.order.application.OrderStatusService;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;

class RefundControllerTest extends ApiTest {

	private Order order;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderStatusService orderStatusService;

	@Autowired
	private PaymentMapper paymentMapper;

	@BeforeEach
	void setUp() {
		order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.set("orderPrice.totalPrice", 1000)
			.setNull("user")
			.sample();

		orderRepository.save(order);
	}

	@AfterEach
	void tearDown() {
		orderRepository.delete(order);
	}

	@DisplayName("결제를 취소할 수 있다.")
	@Test
	void 결제_취소() throws Exception {

		//given
		MockMvcRequestSpecification given = RestAssuredMockMvc.given()
			.mockMvc(standaloneSetup(
					new RefundController(
						paymentMapper,
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
			.post("/payments/refund/{orderId}", 1L)
		.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body(containsString("complete"));
		//@formatter:on

	}
}