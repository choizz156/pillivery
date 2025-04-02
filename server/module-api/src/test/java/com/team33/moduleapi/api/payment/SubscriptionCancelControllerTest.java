package com.team33.moduleapi.api.payment;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.PaymentToken;
import com.team33.modulecore.core.order.domain.Price;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.domain.repository.SubscriptionOrderRepository;
import com.team33.modulecore.core.user.domain.entity.User;
import com.team33.modulecore.core.user.domain.repository.UserRepository;

class SubscriptionCancelControllerTest extends ApiTest {

	@Autowired
	private SubscriptionOrderRepository subscriptionOrderRepository;
	
	@Autowired
	private OrderCommandRepository orderCommandRepository;
	
	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private UserRepository userRepository;


	@DisplayName("구독 주문 취소를 요청할 수 있다.")
	@Test
	void 구독_취소_요청() throws Exception {
		// given
		User user = User.builder().displayName("testUser").email("test@test.com").build();
		User savedUser = userRepository.save(user);
		
		Item item = Item.create(Information.builder()
			.price(new com.team33.modulecore.core.item.domain.Price(1000, 0))
			.productName("테스트 상품")
			.build());
		Item savedItem = itemCommandRepository.save(item);
		
		SubscriptionInfo subscriptionInfo = SubscriptionInfo.of(true, 30);
		OrderItem orderItem = OrderItem.create(savedItem, 1, subscriptionInfo);
		
		OrderCommonInfo commonInfo = OrderCommonInfo.builder()
			.userId(savedUser.getId())
			.price(new Price(10000, 0))
			.paymentToken(PaymentToken.builder().sid("sid").tid("tid").build())
			.orderStatus(OrderStatus.SUBSCRIBE)
			.build();
		
		List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(orderItem);
		
		Order order = Order.builder()
			.orderCommonInfo(commonInfo)
			.isOrderedAtCart(false)
			.totalItemsCount(1)
			.orderItems(orderItems)
			.build();
		
		Order savedOrder = orderCommandRepository.save(order);
		
		SubscriptionOrder subscriptionOrder = SubscriptionOrder.create(savedOrder, orderItem);
		SubscriptionOrder savedSubscriptionOrder = subscriptionOrderRepository.save(subscriptionOrder);
		
		long subscriptionOrderId = savedSubscriptionOrder.getId();

		// when
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
		.when()
			.post("/api/payments/subscriptions/cancel/{subscriptionOrderId}", subscriptionOrderId)
		.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.body("data", equalTo("complete"));
	}
}