package com.team33.moduleapi.api.payment;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.api.payment.dto.RefundDto;
import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.PaymentToken;
import com.team33.modulecore.core.order.domain.Price;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;

class RefundControllerTest extends ApiTest {

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	@Autowired
	private UserRepository userRepository;

	@DisplayName("환불을 요청할 수 있다.")
	@Test
	void 환불_요청() throws Exception {
		// given
		User user = User.builder().displayName("testUser").email("test@test.com").build();
		userRepository.save(user);

		OrderCommonInfo commonInfo = OrderCommonInfo.builder()
			.userId(user.getId())
            .price(new Price(10000, 0))
            .paymentToken(PaymentToken.builder().tid("tid").build())
			.orderStatus(OrderStatus.COMPLETE)
			.build();

		Order testOrder = Order.builder()
			.orderCommonInfo(commonInfo)
			.isOrderedAtCart(false)
			.totalItemsCount(0)
			.orderItems(new ArrayList<>())
			.build();
		Order savedOrder = orderCommandRepository.save(testOrder);
		long orderId = savedOrder.getId();

		RefundDto refundDto = RefundDto.builder()
			.cancelAmount(1000)
			.cancelTaxFreeAmount(0)
			.build();

		// when
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(refundDto)
		.when()
			.post("/api/payments/refund/{orderId}", orderId)
		// then
		.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body("data", equalTo("complete"));
	}
}