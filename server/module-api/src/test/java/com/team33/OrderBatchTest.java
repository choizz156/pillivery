package com.team33;

import static com.team33.modulecore.core.category.domain.CategoryName.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.team33.moduleadmin.service.OrderBatchService;
import com.team33.moduleadmin.service.OrderItemBatchService;
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
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.user.domain.Address;

// @Disabled("로컬에서만 진행되는 테스트")
@Commit
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class OrderBatchTest {

	@Autowired
	private OrderBatchService orderBatchService;

	@Autowired
	private OrderItemBatchService orderItemBatchService;

	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	private List<Order> orders;

	// @BeforeAll
	void setUpEach() {

		var orderValue = new AtomicInteger(0);
		OrderCommonInfo orderCommonInfo = FixtureMonkeyFactory.get().giveMeBuilder(OrderCommonInfo.class)
			.set("receiver", new Receiver(
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
			.sample();

		orders = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.set("isOrderedAtCart", false)
			.setLazy("totalItemsCount", () -> orderValue.get() % 5 + 1)
			.set("orderCommonInfo", orderCommonInfo)
			.setLazy("orderItems", () -> {
				List<OrderItem> items = createOrderItems(1);
				items.forEach(item -> item.addOrder(null));
				return items;
			})
			.sampleList(100);
		orders.forEach(order ->
			order.getOrderItems().forEach(item ->
				item.addOrder(order)));
	}

	@Test
	void orderItem() {
		var itemValue = new AtomicInteger(0);
		Item sample = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.setNull("id")
			.setLazy("name", () -> "상품" + itemValue.addAndGet(1))
			.setLazy("price", () -> 10000 + (itemValue.get() % 5) * 1000)
			.setLazy("description", () -> "상품설명" + itemValue.get())
			.set("categories", new Categories(Set.of(BONE)))
			.sample();
		Item item = itemCommandRepository.save(sample);

		List<OrderItem> orderItems = FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.setNull("id")
			.setNull("order")
			.set("item", item)
			.set("quantity", 1)
			.setNull("subscriptionOrder")
			.set("subscriptionInfo", FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionInfo.class)
				.set("isSubscription", false)
				.set("period", 0)
				.setNull("nextPaymentDate")
				.sample())
			.sampleList(1);

		var orderValue = new AtomicInteger(0);
		List<OrderCommonInfo> orderCommonInfos = FixtureMonkeyFactory.get().giveMeBuilder(OrderCommonInfo.class)
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
			.sampleList(100000);

		AtomicInteger commonInfoIndex = new AtomicInteger(0);
		orders = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.set("isOrderedAtCart", false)
			.setLazy("totalItemsCount", () -> orderValue.get() % 5 + 1)
			.setLazy("orderCommonInfo", () -> orderCommonInfos.get(commonInfoIndex.getAndIncrement()))
			.setLazy("orderItems", () -> orderItems)
			.sampleList(100000);

		orders.forEach(order ->
			order.getOrderItems().forEach(orderItem ->
				orderItem.addOrder(order)));

		orderCommandRepository.saveAll(orders);
	}

	@DisplayName("Identity 전략 jdbc batchInsert 멀티스레드")
	@Test
	void test4() throws Exception {

		orderBatchService.saveAll(orders);
	}

	@DisplayName("Identity 전략 jpa batchInsert 멀티스레드")
	@Test
	void item() throws Exception {

		var itemValue = new AtomicInteger(0);
		Item sample = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.setLazy("productName", () -> "상품" + itemValue.addAndGet(1))
			.setLazy("price", () -> 10000 + (itemValue.get() % 5) * 1000)
			.setLazy("description", () -> "상품설명" + itemValue.get())
			.sample();
		itemCommandRepository.saveAll(List.of(sample));
	}

	@DisplayName("Identity 전략 jpa batchInsert 멀티스레드")
	@Test
	void order() throws Exception {

		orderCommandRepository.saveAll(orders);
	}

	List<OrderItem> createOrderItems(int count) {

		var itemValue = new AtomicInteger(0);
		Item sample = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.setNull("id")
			.setLazy("name", () -> "상품" + itemValue.addAndGet(1))
			.setLazy("price", () -> 10000 + (itemValue.get() % 5) * 1000)
			.setLazy("description", () -> "상품설명" + itemValue.get())
			.sample();
		Item item = itemCommandRepository.save(sample);
		return FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.setNull("id")
			.setNull("order")
			.set("item", item)
			.set("quantity", 1)
			.set("subscriptionInfo", FixtureMonkeyFactory.get().giveMeBuilder(SubscriptionInfo.class)
				.set("isSubscription", false)
				.set("period", 0)
				.setNull("nextPaymentDate")
				.sample())
			.sampleList(count);
	}
}
