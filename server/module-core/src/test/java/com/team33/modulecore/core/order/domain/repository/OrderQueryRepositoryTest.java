package com.team33.modulecore.core.order.domain.repository;

import static com.team33.modulecore.core.category.domain.CategoryName.*;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPage;
import com.team33.modulecore.core.order.dto.OrderPageRequest;
import com.team33.modulecore.core.order.dto.query.OrderItemQueryDto;
import com.team33.modulecore.core.order.dto.query.SubscriptionOrderItemQueryDto;
import com.team33.modulecore.core.order.infra.OrderQueryDslDao;
import com.team33.modulecore.core.user.domain.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class OrderQueryRepositoryTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private OrderQueryRepository orderQueryRepository;
	private User MOCK_USER;

	@BeforeEach
	void beforeAll() {
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
		em.getTransaction().begin();
		orderQueryRepository = new OrderQueryDslDao(new JPAQueryFactory(em));
		persistOrder();
	}

	@AfterEach
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
		orderPageDto.setSize(11);

		var orderPageRequest = OrderPageRequest.of(orderPageDto);

		var orderFindCondition =
			OrderFindCondition.to(1L, OrderStatus.COMPLETE);

		// log.info("Subscription Orders count: {}",
		// 	em.createQuery("SELECT COUNT(o) FROM Order o where o.orderCommonInfo.orderStatus = 'COMPLETE'", Long.class)
		// 		.getSingleResult());
		//
		//when
		Page<OrderItemQueryDto> allOrders =
			orderQueryRepository.findOrdersWithItems(orderPageRequest, orderFindCondition);

		//then
		List<OrderItemQueryDto> content = allOrders.getContent();
		assertThat(content).hasSize(8)
			.isSortedAccordingTo(comparing(OrderItemQueryDto::getOrderId).reversed());
		assertThat(allOrders.getSize()).isEqualTo(11);
		assertThat(allOrders.getTotalPages()).isEqualTo(1);
		assertThat(allOrders.getTotalElements()).isEqualTo(8);
		assertThat(allOrders.getNumberOfElements()).isEqualTo(8);
	}

	@DisplayName("유저의 정기 주문(구독) 목록을 조회할 수 있다.")
	@Test
	void 정기_주문_목록_조회() throws Exception {
		//given
		var orderPageDto1 = new OrderPage();
		orderPageDto1.setPage(1);
		orderPageDto1.setSize(10);

		var orderPageRequest = OrderPageRequest.of(orderPageDto1);
		var orderFindCondition = OrderFindCondition.to(MOCK_USER.getId(), OrderStatus.SUBSCRIPTION);

		persistSubscriptionOrder();



		//when
		Page<SubscriptionOrderItemQueryDto> subscriptionOrderItem =
			orderQueryRepository.findSubscriptionOrderItemsWithItems(orderPageRequest, orderFindCondition);

		//then
		List<SubscriptionOrderItemQueryDto> content = subscriptionOrderItem.getContent();


		assertThat(content).hasSize(8)
			.extracting("itemName")
			.as("page 1, size 8, offset 0, 내림차순")
			.contains("title5", Index.atIndex(3));

	}

	@DisplayName("구독 정보를 조회할 수 있다.")
	@Test
	void 구독_정보_조회() throws Exception {
		//given
		SubscriptionOrder subscriptionOrder = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionOrder.class)
			.setNull("id")
			.setNull("orderItem")
			.set("subscriptionInfo", SubscriptionInfo.of(true, 60))
			.sample();

		em.persist(subscriptionOrder);

		//when
		boolean isSubscriptionById = orderQueryRepository.findIsSubscriptionById(subscriptionOrder.getId());

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
			.set("orderCommonInfo.paymentToken.tid", "tid")
			.sample();

		em.persist(order);

		//when
		String tid = orderQueryRepository.findTid(order.getId());

		//then
		assertThat(tid).isEqualTo("tid");
	}

	private void persistSubscriptionOrder() {

		for (long i = 1; i <= 8; i++) {
			Order order = em.find(Order.class, i);
			order.getOrderItems().stream()
				.map(orderItem ->
					{
						SubscriptionOrder subscriptionOrder = SubscriptionOrder.create(order, orderItem);
						return subscriptionOrder;
					}
				).forEach(em::persist);
		}
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

	private void persistMockOrder(List<Item> mockItems) {

		OrderContext orderContext1 = OrderContext.builder()
			.userId(1L)
			.isOrderedCart(false)
			.isSubscription(false)
			.build();

		mockItems.stream()
			.takeWhile(item -> item.getId() <= 8)
			.map(item -> OrderItem.create(item, 1, new SubscriptionInfo())
			)
			.map(orderItem ->
				Order.create(
					List.of(orderItem),
					OrderCommonInfo.createFromContext(List.of(orderItem), orderContext1),
					orderContext1
				)
			)
			.forEach(order -> {
				order.changeOrderStatus(OrderStatus.COMPLETE);
				em.persist(order);
			});
	}
}