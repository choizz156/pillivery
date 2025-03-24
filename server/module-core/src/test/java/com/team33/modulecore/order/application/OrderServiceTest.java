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
import org.springframework.context.ApplicationContext;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.common.UserFindHelper;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.application.OrderCreateService;
import com.team33.modulecore.core.order.application.OrderPaymentCodeService;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.order.application.OrderSubscriptionService;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.OrderPrice;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.Receiver;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.events.CartRefreshedEvent;
import com.team33.modulecore.core.order.events.ItemSaleCountedEvent;
import com.team33.modulecore.order.mock.FakeOrderCommandRepository;
import com.team33.modulecore.core.payment.application.cancel.CancelSubscriptionService;
import com.team33.modulecore.core.payment.application.refund.RefundService;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;
import com.team33.modulecore.core.user.domain.Address;
import com.team33.modulecore.core.user.domain.entity.User;

@TestInstance(Lifecycle.PER_CLASS)
class OrderServiceTest {

	private User user;
	private OrderCommandRepository orderCommandRepository;

	@BeforeAll
	void beforeAll() {
		getMockItem();
		user = getMockUser();
		orderCommandRepository = new FakeOrderCommandRepository();
	}

	@DisplayName("order 객체를 생성할 수 있다.")
	@Test
	void callOrderTest() throws Exception {
		//given
		OrderContext orderContext = OrderContext.builder()
			.isSubscription(false)
			.isOrderedCart(true)
			.userId(1L)
			.receiver(Receiver.builder()
				.realName("홍홍홍")
				.phone("010-1234-5678")
				.address(new Address("인천 부평구", "한국아파트 101"))
				.build()
			)
			.build();

		var orderItems = getMockOrderItems();
		var userFindHelper = mock(UserFindHelper.class);
		given(userFindHelper.findUser(anyLong())).willReturn(user);

		var orderService =
			new OrderCreateService(orderCommandRepository);

		//when
		Order order = orderService.callOrder(orderItems, orderContext);

		//then
		assertThat(order.getOrderPrice()).isEqualTo(new OrderPrice(orderItems));
		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REQUEST);
		assertThat(order.getOrderItems()).hasSize(3);
		assertThat(order.getTotalItemsCount()).isEqualTo(3);
		assertThat(order.getUserId()).isEqualTo(user.getId());
	}

	@DisplayName("일반 주문의 주문 상태를 구독 중으로 바꿀 수 있다.")
	@Test
	void 주문_상태_변경1() throws Exception {
		//given
		var order = orderCommandRepository.save(getNoCartOrder());

		ApplicationContext applicationContext = mock(ApplicationContext.class);
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);
		CancelSubscriptionService cancelSubscriptionService = mock(CancelSubscriptionService.class);
		RefundService refundService = mock(RefundService.class);

		when(orderFindHelper.findOrder(anyLong())).thenReturn(order);

		var orderService =
			new OrderStatusService(
				applicationContext,
				new OrderFindHelper(orderCommandRepository, null),
				new OrderPaymentCodeService(orderFindHelper),
				cancelSubscriptionService,
				refundService
			);

		//when
		orderService.processSubscriptionStatus(order.getId(), "sid");

		//then
		verify(applicationContext, times(1)).publishEvent(any(CartRefreshedEvent.class));
		verify(applicationContext, times(1)).publishEvent(any(ItemSaleCountedEvent.class));

		assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.SUBSCRIBE);
	}

	@DisplayName("카트 주문의 주문 상태를 구독 중으로 바꿀 수 있다.")
	@Test
	void 주문_상태_변경2() throws Exception {
		//given
		var order = orderCommandRepository.save(getCartOrder());
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		CancelSubscriptionService cancelSubscriptionService = mock(CancelSubscriptionService.class);
		RefundService refundService = mock(RefundService.class);

		OrderFindHelper orderFindHelper = new OrderFindHelper(orderCommandRepository, null);

		var orderService =
			new OrderStatusService(
				applicationContext,
				orderFindHelper,
				new OrderPaymentCodeService(orderFindHelper),
				cancelSubscriptionService,
				refundService
			);

		//when
		orderService.processSubscriptionStatus(order.getId(), "sid");

		//then
		verify(applicationContext, times(1)).publishEvent(any(CartRefreshedEvent.class));
		verify(applicationContext, times(1)).publishEvent(any(ItemSaleCountedEvent.class));

		assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.SUBSCRIBE);
	}

	@DisplayName("일반 주문 상태를 주문 완료로 바꿀 수 있다.")
	@Test
	void 주문_상태_변경4() throws Exception {
		//given
		Order sample = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("isOrderedAtCart", false)
			.set("isSubscription", true)
			.setNull("receiver")
			.setNull("orderItems")
			.sample();

		var order = orderCommandRepository.save(sample);

		ApplicationContext applicationContext = mock(ApplicationContext.class);

		var orderService =
			new OrderStatusService(applicationContext, new OrderFindHelper(orderCommandRepository, null), null, null,
				null);

		//when
		orderService.processOneTimeStatus(order.getId());

		//then
		verify(applicationContext, times(1)).publishEvent(any(CartRefreshedEvent.class));
		verify(applicationContext, times(1)).publishEvent(any(ItemSaleCountedEvent.class));

		assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.COMPLETE);
	}

	@DisplayName("카트 주문 상태를 주문 완료로 바꿀 수 있다.")
	@Test
	void 주문_상태_변경5() throws Exception {
		//given
		Order sample = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("isOrderedAtCart", true)
			.set("isSubscription", false)
			.setNull("receiver")
			.setNull("orderItems")
			.sample();

		var order = orderCommandRepository.save(sample);
		ApplicationContext applicationContext = mock(ApplicationContext.class);

		var orderService =
			new OrderStatusService(applicationContext, new OrderFindHelper(orderCommandRepository, null), null, null,
				null);

		//when
		orderService.processOneTimeStatus(order.getId());

		//then
		assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.COMPLETE);

	}

	@DisplayName("주문 상태를 취소로 바꿀 수 있다.")
	@Test
	void 주문_상태_변경6() throws Exception {
		//given
		Order sample = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("isOrderedAtCart", true)
			.set("isSubscription", false)
			.setNull("receiver")
			.setNull("orderItems")
			.sample();

		RefundContext refundContext = RefundContext.builder()
			.cancelAmount(1000)
			.cancelTaxFreeAmount(0)
			.build();

		var order = orderCommandRepository.save(sample);

		RefundService refundService = mock(RefundService.class);
		ApplicationContext applicationContext = mock(ApplicationContext.class);

		var orderService =
			new OrderStatusService(applicationContext, new OrderFindHelper(orderCommandRepository, null), null, null,
				refundService);

		//when
		orderService.processCancel(order.getId(), refundContext);

		//then

		assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.REFUND);
		verify(refundService, times(1)).refund(order.getId(), refundContext);
	}

	@DisplayName("order 객체에 tid를 저장할 수 있다.")
	@Test
	void 주문_상태_변경7() throws Exception {
		//given
		Order sample = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("isOrderedAtCart", true)
			.set("isSubscription", false)
			.setNull("receiver")
			.setNull("orderItems")
			.sample();

		var order = orderCommandRepository.save(sample);

		var orderService =
			new OrderPaymentCodeService(new OrderFindHelper(orderCommandRepository, null));

		//when
		orderService.addTid(order.getId(), "tid");

		//then
		assertThat(order.getPaymentId().getTid()).isNotNull();
	}

	@DisplayName("정기 결제 중인 아이템의 수량을 조정할 수 있다.")
	@Test
	void 정기_구독_수량_조정() throws Exception {
		//given
		var order = orderCommandRepository.save(getMockOrderWithOrderItem());

		var orderService =
			new OrderSubscriptionService(new OrderFindHelper(orderCommandRepository, null));

		//when
		orderService.changeSubscriptionItemQuantity(
			order.getId(),
			order.getOrderItems().get(0).getId(),
			2
		);

		//then
		assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(2);
		assertThat(order.getTotalQuantity()).isEqualTo(8);
		assertThat(order.getOrderPrice().getTotalPrice()).isEqualTo(8000);
		assertThat(order.getOrderPrice().getTotalDiscountPrice()).isEqualTo(4000);
	}

	@Test
	void 주문_복사() throws Exception {
		//given
		var orderService =
			new OrderCreateService(orderCommandRepository);
		Order order = getMockOrderWithOrderItem();

		//when
		Order copyOrder = deepCopy(order);

		//then
		assertThat(copyOrder).usingRecursiveComparison()
			.ignoringFields("id", "createdAt", "updatedAt")
			.isEqualTo(order);
	}

	private Order deepCopy(Order order) {
		Order newOrder = new Order(order);
		orderCommandRepository.save(newOrder);

		return newOrder;
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
			.set("id", 1L)
			.set("cartId", null)
			.sample();
	}

	private Order getNoCartOrder() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("isOrderedAtCart", false)
			.set("isSubscription", false)
			.setNull("paymentCode.sid")
			.sample();
	}

	private Order getCartOrder() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("isOrderedAtCart", true)
			.set("isSubscription", true)
			.setNull("paymentCode.sid")
			.sample();
	}

	private Order getMockOrderWithOrderItem() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("orderItems", getMockOrderItems())
			.set("totalQuantity", 9)
			.set("orderPrice", new OrderPrice(getMockOrderItems()))
			.set("user", user)
			.set("paymentCode.sid", "sid")
			.sample();
	}

	private List<OrderItem> getMockOrderItems() {
		return FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.set("item", getMockItem())
			.set("quantity", 3)
			.set("item.information.price.realPrice", 1000)
			.set("item.information.price.discountPrice", 500)
			.sampleList(3);
	}
}
