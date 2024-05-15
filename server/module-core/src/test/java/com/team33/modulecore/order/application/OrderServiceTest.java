package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.order.mock.FakeOrderRepository;
import com.team33.modulecore.user.domain.User;

@TestInstance(Lifecycle.PER_CLASS)
class OrderServiceTest {

	private User user;
	private OrderRepository orderRepository;

	@BeforeAll
	void beforeAll() {
		getMockItem();
		user = getMockUser();
		orderRepository = new FakeOrderRepository();
	}

	@DisplayName("단건 order 객체를 생성할 수 있다.")
	@Test
	void callOrderTest() throws Exception {

		//given
		var orderItems = getMockOrderItems();
		var userFindHelper = mock(UserFindHelper.class);
		given(userFindHelper.findUser(anyLong())).willReturn(user);
		var orderService =
			new OrderService(orderRepository, null, null, userFindHelper);

		//when
		Order order = orderService.callOrder(orderItems, false, 1L);

		//then

		assertThat(order.getOrderPrice()).isEqualTo(new OrderPrice(orderItems));
		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REQUEST);
		assertThat(order.getOrderItems()).hasSize(3);
		assertThat(order.getTotalItems()).isEqualTo(3);
		assertThat(order.getUser()).isEqualTo(user);
	}

	@DisplayName("주문 상태를 구독 중으로 바꿀 수 있다.")
	@Test
	void 주문_상태_변경1() throws Exception {
		//given
		var order = orderRepository.save(getMockOrder());
		var orderService =
			new OrderService(orderRepository, null, null, mock(UserFindHelper.class));

		//when
		orderService.changeOrderStatusToSubscribe(order.getId());

		//then
		assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.SUBSCRIBE);
	}

	@DisplayName("주문 상태를 주문 완료로 바꿀 수 있다.")
	@Test
	void 주문_상태_변경2() throws Exception {
		var order = orderRepository.save(getMockOrder());
		var orderService =
			new OrderService(orderRepository, null, null, mock(UserFindHelper.class));

		//when
		orderService.changeOrderStatusToComplete(order.getId());

		//then
		assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.COMPLETE);
	}

	@DisplayName("정기 구독 아이템의 수량을 조정할 수 있다.")
	@Test
	void 정기_구독_수량_조정() throws Exception {
		//given
		var order = orderRepository.save(getMockOrderWithOrderItem());

		var orderService =
			new OrderService(orderRepository, null, null, mock(UserFindHelper.class));

		//when
		orderService.changeSubscriptionItemQuantity(
			order.getId(),
			order.getOrderItems().get(0).getId(),
			2
		);

		//then
		List<OrderItem> orderItems = order.getOrderItems();
		assertThat(orderItems).hasSize(3);
		OrderItem target = orderItems.get(0);
		assertThat(target.getQuantity()).isEqualTo(2);

	}

	@DisplayName("주문을 취소할 수 있다.")
	@Test
	void 주문_취소() throws Exception {
		//given
		var order = orderRepository.save(getMockOrderWithOrderItem());

		var orderService =
			new OrderService(orderRepository, null, null, mock(UserFindHelper.class));

		//when
		orderService.cancelOrder(order.getId());

		//then
		assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.CANCEL);
	}

	private Item getMockItem() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("statistics.sales", 1)
			.set("information.price.discountRate", 3D)
			.set("information.price.realPrice", 3000)
			.set("information.productName", "mockItem")
			.sample();
	}

	private User getMockUser() {
		return FixtureMonkeyFactory.get().giveMeBuilder(User.class)
			.set("id", null)
			.set("cart", null)
			.sample();
	}

	private List<OrderItem> getMockOrderItems() {
		return FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.set("item", getMockItem())
			.sampleList(3);
	}

	private Order getMockOrder() {
		return FixtureMonkeyFactory.get().giveMeOne(Order.class);
	}

	private Order getMockOrderWithOrderItem() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("orderItems", getMockOrderItems())
			.set("user", null)
			.set("orderPrice", null)
			.sample();
	}
}