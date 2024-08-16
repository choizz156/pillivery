package com.team33.moduleapi.controller.quartz;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.UserAccount;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.application.OrderCreateService;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.application.OrderQueryService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

class ScheduleControllerTest extends ApiTest {

	@MockBean(name = "orderService")
	private OrderCreateService orderCreateService;

	@MockBean
	private OrderQueryService orderQueryService;

	@MockBean
	private OrderCommandRepository orderRepository;

	@MockBean
	private OrderItemService orderItemService;

	private Order order;
	private OrderItem orderItem;
	private final ZonedDateTime now = ZonedDateTime.of(2028, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));

	@BeforeEach
	void setUp() {

		Item item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information.productName", "testItem")
			.sample();

		orderItem = FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.set("id", 1L)
			.set("subscriptionInfo.period", 30)
			.set("subscriptionInfo.paymentDay", now)
			.set("subscriptionInfo.nextPaymentDay", now.plusDays(30))
			.set("quantity", 1)
			.set("item", item)
			.sample();

		order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("userId", 1L)
			.set("orderItems", new ArrayList<>())
			.set("id", 1L)
			.set("createdAt", now)
			.sample();

		order.getOrderItems().add(orderItem);
		orderItem.addOrder(order);

		given(orderQueryService.findOrder(anyLong()))
			.willReturn(order);

		given(orderRepository.findById(anyLong()))
			.willReturn(Optional.ofNullable(order));

		given(orderItemService.findOrderItem(anyLong()))
			.willReturn(orderItem);

		orderItem.applyNextPaymentDate(orderItem.getPaymentDay().plusDays(60));

		doNothing().when(orderItemService).updateNextPaymentDate(any(), anyLong());
	}

	@DisplayName("요청 시 스케쥴러가 설정된다.")
	@Test
	void test1() throws Exception {

		Long orderId = order.getId();
		//@formatter:off
        given()
                .log().all()
        .when()
                .post("/schedules/{orderId}", orderId)
        .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .log().all();
        //@formatter:on
	}

	@DisplayName("스케쥴을 수정할 수 있다.(30 -> 60)")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void test2() throws Exception {

		String token = super.getToken();

		ExtractableResponse<Response> response =
			//@formatter:off
            given()
                    .log().all()
                    .header("Authorization", token)
                    .param("period", 60)
                    .param("orderId", 1L)
                    .param("itemOrderId", 1L)
            .when()
                    .patch("/schedules")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
            //@formatter:on

		String paymentDay = response.jsonPath().get("data.nextPaymentDay");
		String year = paymentDay.substring(0, 4);
		String month = paymentDay.substring(6, 7);
		String day = paymentDay.substring(9, 10);

		assertThat(year).isEqualTo(String.valueOf(orderItem.getPaymentDay().getYear()));
		assertThat(month).isEqualTo(
			String.valueOf(orderItem.getPaymentDay().getMonth().getValue())
		);
		assertThat(day).isEqualTo(
			String.valueOf(orderItem.getPaymentDay().getDayOfMonth())
		);
	}

	@DisplayName("스케쥴을 취소할 수 있다.")
	@UserAccount({"test", "010-0000-0000"})
	@Test
	void test3() throws Exception {

		String token = super.getToken();

		ExtractableResponse<Response> response =
			//@formatter:off
            given()
                    .log().all()
                    .header("Authorization", token)
                    .param("orderId", 1L)
                    .param("itemOrderId", 1L)
            .when()
                    .delete("/schedules")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
            //@formatter:on

		assertThat(response.body().asString()).isNotBlank();
	}
}