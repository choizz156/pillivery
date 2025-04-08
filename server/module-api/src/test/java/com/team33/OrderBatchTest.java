package com.team33;

import static com.team33.modulecore.core.category.domain.CategoryName.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.team33.moduleadmin.service.SubscriptionOrderBatchService;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.modulecore.core.category.domain.Categories;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.Price;
import com.team33.modulecore.core.order.domain.Receiver;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.domain.repository.SubscriptionOrderRepository;
import com.team33.modulecore.core.user.domain.Address;

// @Disabled("로컬에서만 진행되는 테스트")
@Commit
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class OrderBatchTest {

	public static final int SIZE = 99999;

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	@Autowired
	private SubscriptionOrderRepository subscriptionOrderRepository;

	private List<Order> orders;
	private Item item;
	private List<OrderItem> orderItems;
	private List<OrderCommonInfo> orderCommonInfos;
	@Autowired
	private SubscriptionOrderBatchService subscriptionOrderBatchService;

	@BeforeAll
	void beforeAll() {

		var itemValue = new AtomicInteger(0);
		Item sample = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.setNull("id")
			.setLazy("name", () -> "상품" + itemValue.addAndGet(1))
			.setLazy("price", () -> 10000 + (itemValue.get() % 5) * 1000)
			.setLazy("description", () -> "상품설명" + itemValue.get())
			.set("categories", new Categories(Set.of(BONE)))
			.sample();

		item = itemCommandRepository.save(sample);

		orderItems = FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.setNull("id")
			.setNull("order")
			.set("item", item)
			.set("quantity", 1)
			.setNull("subscriptionOrder")
			.set("subscriptionInfo", FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionInfo.class)
				.set("isSubscription", true)
				.set("period", 60)
				.setNull("nextPaymentDate")
				.sample())
			.sampleList(1);

		var orderValue = new AtomicInteger(0);
		orderCommonInfos = FixtureMonkeyFactory.get().giveMeBuilder(OrderCommonInfo.class)
			.setLazy("receiver", () -> new Receiver(
				"수령인" + orderValue.getAndIncrement(),
				"010-1234-" + String.format("%04d", orderValue.intValue()),
				new Address("서울시 강남구", "테헤란로 " + orderValue.intValue() + "길")
			))
			.setLazy("mainItemName", () -> "테스트상품" + orderValue.getAndIncrement())
			.set("orderStatus", OrderStatus.COMPLETE)
			.setLazy("paymentToken.sid", () -> "SID_" + orderValue.intValue())
			.setLazy("paymentToken.tid", () -> "TID_" + orderValue.intValue())
			.set("price", new Price(
				15000,
				2000,
				13000
			))
			.setLazy("userId", () -> (long)orderValue.intValue())
			.setLazy("totalQuantity", () -> orderValue.get() % 3 + 1)
			.sampleList(SIZE);
	}

	@DisplayName("order batch jpa")
	@Test
	void test1() {

		AtomicInteger commonInfoIndex = new AtomicInteger(0);
		orders = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.set("isOrderedAtCart", false)
			.setLazy("totalItemsCount", () -> commonInfoIndex.get() % 5 + 1)
			.setLazy("orderCommonInfo", () -> orderCommonInfos.get(commonInfoIndex.getAndIncrement()))
			.setLazy("orderItems", () -> orderItems)
			.sampleList(SIZE);

		orders.forEach(order ->
			order.getOrderItems().forEach(orderItem ->
				orderItem.addOrder(order)));

		orderCommandRepository.saveAll(orders);
	}

	@DisplayName("subscriptionOrder batch jpa")
	@Test
	void test3() throws Exception {
		//given
		SubscriptionInfo sample = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionInfo.class)
			.set("isSubscription", true)
			.set("period", 60)
			.setNull("nextPaymentDate")
			.sample();

		List<OrderItem> orderItems = entityManager.createQuery(
				"SELECT oi FROM OrderItem oi WHERE oi.id BETWEEN 1 AND 99999",
				OrderItem.class)
			.getResultList();

		var orderItemId = new AtomicInteger(0);
		var orderCommonId = new AtomicInteger(0);
		List<SubscriptionOrder> subscriptionOrders = FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionOrder.class)
			.setNull("id")
			.set("subscriptionInfo", sample)
			.setLazy("orderCommonInfo", () -> orderCommonInfos.get(orderCommonId.getAndIncrement()))
			.setLazy("orderItem", () -> orderItems.get(orderItemId.getAndIncrement()))
			.sampleList(99999);

		subscriptionOrderBatchService.saveAll(subscriptionOrders);
	}

	@Test
	void test4() throws Exception {

		Order order = entityManager.find(Order.class, 88L);
		List<OrderItem> orderItems = FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.setNull("id")
			.setNull("order")
			.set("item", item)
			.set("quantity", 1)
			.setNull("subscriptionOrder")
			.set("subscriptionInfo", FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionInfo.class)
				.set("isSubscription", true)
				.set("period", 60)
				.setNull("nextPaymentDate")
				.sample())
			.sampleList(20);
		orderItems.forEach(order::addOrderItems);

		entityManager.persist(order);
	}
}
