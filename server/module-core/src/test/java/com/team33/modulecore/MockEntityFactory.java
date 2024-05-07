package com.team33.modulecore;

import static com.team33.modulecore.category.domain.CategoryName.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.SubscriptionItemInfo;
import com.team33.modulecore.user.domain.User;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockEntityFactory {

	private EntityManager entityManager;

	private User user;

	private MockEntityFactory(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public static MockEntityFactory of(EntityManager entityManager) {
		return new MockEntityFactory(entityManager);
	}

	public static MockEntityFactory of() {
		return new MockEntityFactory();
	}

	public void persistItem() {
		persistMockItems();
	}

	public void persistOrder() {
		user = getMockUser();
		entityManager.persist(user);

		List<Item> mockItems = getMockItems();
		mockItems.forEach(entityManager::persist);

		persistMockOrder(user, mockItems);
	}

	public Item getMockItem() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("statistics.sales", 1)
			.set("information.price.discountRate", 3L)
			.set("information.price.realPrice", 3000)
			.set("information.productName", "mockItem")
			.sample();
	}

	public List<OrderItem> getMockOrderItems() {
		return FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.set("item", getMockItem())
			.sampleList(3);
	}

	public OrderItem getMockOrderItem() {
		return FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.set("item", getMockItem())
			.sample();
	}

	public Order getMockOrder() {
		return FixtureMonkeyFactory.get().giveMeOne(Order.class);
	}

	public Order getMockOrderWithOrderItem() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("orderItems", List.of(getMockOrderItem()))
			.set("user", null)
			.set("orderPrice", null)
			.sample();
	}

	public List<Order> getMockOrders() {
		OrderItem mockOrderItem = getMockOrderItem();
		List<Order> orders1 = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("orderItems", List.of(mockOrderItem))
			.set("user", null)
			.set("orderStatus", OrderStatus.COMPLETE)
			.set("orderPrice", null)
			.sampleList(7);

		List<Order> orders2 = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("orderItems", List.of(mockOrderItem))
			.set("user", null)
			.set("orderStatus", OrderStatus.SUBSCRIBE)
			.set("orderPrice", null)
			.sampleList(7);
		orders1.addAll(orders2);
		return orders1;
	}

	public User getMockUser() {
		return FixtureMonkeyFactory.get().giveMeBuilder(User.class)
			.set("id", null)
			.set("cart", null)
			.sample();
	}

	public User getPersistedUser() {
		return user;
	}

	private void persistMockOrder(User user, List<Item> mockItems) {
		mockItems.stream()
			.takeWhile(item -> item.getId() <= 8)
			.map(item -> OrderItem.create(
				item,
				SubscriptionItemInfo.of(false, 60),
				1
			))
			.map(orderItem -> Order.create(List.of(orderItem), false, user))
			.forEach(order -> {
				order.changeOrderStatus(OrderStatus.COMPLETE);
				entityManager.persist(order);
			});

		mockItems.stream()
			.dropWhile(item -> item.getId() <= 8)
			.map(item -> OrderItem.create(
				item,
				SubscriptionItemInfo.of(true, 60),
				1
			))
			.map(orderItem -> Order.create(List.of(orderItem), true, user))
			.forEach(order -> {
				order.changeOrderStatus(OrderStatus.SUBSCRIBE);
				entityManager.persist(order);
			});
	}

	private List<Item> getMockItems() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", null)
			.set("information.price.discountRate", 1D)
			.sampleList(5);
	}

	private void persistMockItems() {

		var value = new AtomicInteger(0);
		var value1 = new AtomicReference<>(1D);

		List<Item> items = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", null)
			.setLazy("statistics.sales", () -> value.addAndGet(1))
			.setLazy("information.price.discountRate", () -> value1.getAndSet(value1.get() + 1D))
			.setLazy("information.price.realPrice", () -> value.intValue() * 1000)
			.setLazy("information.productName", () -> "title" + value)
			.set("categoryNames", Set.of(EYE))
			.sampleList(15);

		var value2 = new AtomicInteger(9);
		var items2 = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", null)
			.set("statistics.sales", 0)
			.set("information.price.discountRate", 0D)
			.set("information.price.realPrice", 1)
			.set("categoryNames", Set.of(BONE))
			.setLazy("information.productName", () -> "test" + value2.addAndGet(1))
			.sampleList(3);

		items.addAll(items2);
		items.forEach(entityManager::persist);
	}
}
