package com.team33.modulecore.order.domain.repository;

import static com.team33.modulecore.category.domain.CategoryName.*;
import static java.util.Comparator.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.core.data.Index;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Page;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.SubscriptionInfo;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.dto.OrderContext;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPage;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.order.infra.OrderQueryDslDao;
import com.team33.modulecore.user.domain.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
class OrderQueryRepositoryTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private OrderQueryRepository orderQueryRepository;
	private User MOCK_USER;

	@BeforeAll
	void beforeAll() {
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
		em.getTransaction().begin();
		orderQueryRepository = new OrderQueryDslDao(new JPAQueryFactory(em));
		persistOrder();
	}

	@AfterAll
	void afterAll() {
		em.getTransaction().rollback(); // 커넥션 반납용 롤백
		em.close();
		emf.close();
	}

	@DisplayName("유저가 구매한 주문 목록을 조회할 수 있다.")
	@Test
	void 주문_조회() throws Exception {
		//given
		var orderPageDto = new OrderPage();
		orderPageDto.setPage(1);
		orderPageDto.setSize(10);

		var orderPageRequest = OrderPageRequest.of(orderPageDto);

		var orderFindCondition =
			OrderFindCondition.to(1L, OrderStatus.REQUEST);

		//when
		Page<Order> allOrders =
			orderQueryRepository.findOrders(orderPageRequest, orderFindCondition);

		//then
		List<Order> content = allOrders.getContent();
		assertThat(content).hasSize(10)
			.isSortedAccordingTo(comparing(Order::getId).reversed());
		assertThat(allOrders.getSize()).isEqualTo(10);
		assertThat(allOrders.getTotalPages()).isEqualTo(2);
		assertThat(allOrders.getTotalElements()).isEqualTo(16);
		assertThat(allOrders.getNumberOfElements()).isEqualTo(10);
	}

	@DisplayName("유저의 정기 주문(구독) 목록을 조회할 수 있다.")
	@Test
	void 정기_주문_목록_조회() throws Exception {
		//given
		var orderPageDto1 = new OrderPage();
		orderPageDto1.setPage(1);
		orderPageDto1.setSize(10);

		var orderPageRequest = OrderPageRequest.of(orderPageDto1);

		var orderFindCondition = OrderFindCondition.to(MOCK_USER.getId(), OrderStatus.SUBSCRIBE);

		//when
		List<OrderItem> subscriptionOrderItem =
			orderQueryRepository.findSubscriptionOrderItem(orderPageRequest, orderFindCondition);

		//then
		assertThat(subscriptionOrderItem).hasSize(8)
			.extracting("item.information.productName")
			.as("page 1, size 8, offset 0, 내림차순")
			.contains("test13", Index.atIndex(3));

	}

	@DisplayName("구독 정보를 조회할 수 있다.")
	@Test
	void 구독_정보_조회() throws Exception {
		//given
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.set("paymentCode.sid", "sid")
			.set("paymentCode.tid", "tid")
			.setNull("orderItems")
			.set("isSubscription", true)
			.sample();

		em.persist(order);

		//when
		boolean isSubscriptionById = orderQueryRepository.findIsSubscriptionById(order.getId());

		//then
		assertThat(isSubscriptionById).isTrue();
	}

	@DisplayName("tid를 조회할 수 있다.")
	@Test
	void tid_조회() throws Exception {

		//given
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.setNull("user")
			.set("paymentCode.tid", "tid").sample();

		em.persist(order);

		//when
		String tid = orderQueryRepository.findTid(order.getId());

		//then
		assertThat(tid).isEqualTo("tid");
	}

	private void persistOrder() {
		MOCK_USER = getMockUser();
		em.persist(MOCK_USER);

		List<Item> mockItems = getMockItems();
		mockItems.forEach(em::persist);

		persistMockOrder(mockItems);
	}

	private User getMockUser() {
		return FixtureMonkeyFactory.get().giveMeBuilder(User.class)
			.set("id", null)
			.sample();
	}

	private List<Item> getMockItems() {
		var value = new AtomicInteger(0);
		var value1 = new AtomicReference<>(1D);

		var items = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", null)
			.setLazy("statistics.sales", () -> value.addAndGet(1))
			.setLazy("information.price.discountRate", () -> value1.getAndSet(value1.get() + 1D))
			.setLazy("information.price.realPrice", () -> value.intValue() * 1000)
			.setLazy("information.productName", () -> "title" + value)
			.set("itemCategory", Set.of(EYE))
			.set("categories", null)
			.sampleList(8);

		var items2 = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", null)
			.set("statistics.sales", 0)
			.set("information.price.discountRate", 0D)
			.set("information.price.realPrice", 1)
			.set("itemCategory", Set.of(BONE))
			.set("categories", null)
			.setLazy("information.productName", () -> "test" + value.addAndGet(1))
			.sampleList(8);

		items.addAll(items2);
		return items;
	}

	private void persistMockOrder( List<Item> mockItems) {
		OrderContext orderContext1 = OrderContext.builder()
			.userId(1L)
			.isOrderedCart(false)
			.isSubscription(false)
			.build();

		mockItems.stream()
			.takeWhile(item -> item.getId() <= 8)
			.map(item -> OrderItem.create(
					item,
					SubscriptionInfo.of(false, 60),
					1
				)
			)
			.map(orderItem -> Order.create(List.of(orderItem), orderContext1))
			.forEach(order -> {
				order.changeOrderStatus(OrderStatus.COMPLETE);
				em.persist(order);
			});

		OrderContext orderContext2 = OrderContext.builder()
			.userId(1L)
			.isOrderedCart(false)
			.isSubscription(true)
			.build();

		mockItems.stream()
			.dropWhile(item -> item.getId() <= 8)
			.map(item -> OrderItem.create(
				item,
				SubscriptionInfo.of(true, 60),
				1
			))
			.map(orderItem -> Order.create(List.of(orderItem), orderContext2))
			.forEach(order -> {
				order.changeOrderStatus(OrderStatus.SUBSCRIBE);
				em.persist(order);
			});
	}
}