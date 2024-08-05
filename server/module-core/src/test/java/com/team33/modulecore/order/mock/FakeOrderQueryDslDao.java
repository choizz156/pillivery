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
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPageRequest;

public class FakeOrderQueryDslDao implements OrderQueryRepository {

	private Map<Long, Order> orders;

	public FakeOrderQueryDslDao() {
		this.orders = new HashMap<>();
		List<Order> mockOrders = getMockOrders();
		mockOrders.forEach(order -> orders.put(order.getId(), order));
	}

	@Override
	public Page<Order> findOrders(
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
	public List<OrderItem> findSubscriptionOrderItems(
		OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition
	) {
		return orders.values().stream()
			.filter(order -> order.getOrderStatus() == OrderStatus.SUBSCRIBE)
			.map(Order::getOrderItems)
			.flatMap(List::stream)
			.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public OrderItem findSubscriptionOrderItemBy(long id) {
		return null;
	}

	@Override
	public Order findById(long id) {
		return null;
	}

	@Override
	public boolean findIsSubscriptionById(long orderId) {
		return orders.get(orderId).getSid() != null;
	}

	@Override
	public String findTid(long orderId) {
		return orders.get(orderId).getTid();
	}

	private List<Order> getMockOrders() {
		OrderItem mockOrderItem = getMockOrderItem();

		return FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.set("orderItems", List.of(mockOrderItem))
			.set("orderStatus", OrderStatus.SUBSCRIBE)
			.set("paymentCode.sid", "sid")
			.set("paymentCode.tid", "tid")
			.set("orderPrice", null)
			.sampleList(7);
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
