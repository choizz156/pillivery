package com.team33.modulecore.order.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;

public class FakeOrderQueryDslDao implements OrderQueryRepository {

	private Map<Long, Order> orders;

	public FakeOrderQueryDslDao() {
		this.orders = new HashMap<>();
		List<Order> mockOrders = getMockOrders();
		mockOrders.forEach(order -> orders.put(order.getId(), order));
	}

	@Override
	public Page<Order> searchOrders(
		OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition
	) {

		List<Order> orderList = new ArrayList<>();

		for (Map.Entry<Long, Order> entry : orders.entrySet()) {
			orderList.add(entry.getValue());
		}

		return new PageImpl<>(
			orderList.size() < pageRequest.getSize()
				? orderList
				: orderList.subList(0, pageRequest.getSize()),
			PageRequest.of(pageRequest.getPage() - 1, pageRequest.getSize()),
			orderList.size()
		);
	}

	@Override
	public List<OrderItem> findSubscriptionOrderItem(
		OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition
	) {
		return orders.values().stream()
			.filter(order -> order.getOrderStatus() == OrderStatus.SUBSCRIBE)
			.map(Order::getOrderItems)
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	@Override
	public Order findById(Long id) {
		return null;
	}

	private List<Order> getMockOrders() {
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

	private OrderItem getMockOrderItem() {
		return FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.set("item", getMockItem())
			.sample();
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
}
