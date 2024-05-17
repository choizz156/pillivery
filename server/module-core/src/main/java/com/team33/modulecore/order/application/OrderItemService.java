package com.team33.modulecore.order.application;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.ItemFindHelper;
import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.item.application.ItemCommandService;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.dto.OrderItemServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemService {

	private final OrderFindHelper orderFindHelper;
	private final ItemFindHelper itemFindHelper;
	private final ItemCommandService itemCommandService;

	@Transactional(readOnly = true)
	public List<OrderItem> toOrderItems(List<OrderItemServiceDto> dtos) {
		return dtos.stream()
			.map(this::getOrderItemSingle)
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	public void setItemPeriod(int period, OrderItem orderItem) {
		orderItem.addPeriod(period);
		log.error("주기변경 = {}", orderItem.getPeriod());
	}

	public OrderItem delayDelivery(Long orderId, Integer delay, OrderItem orderItem1) {

		Order order = orderFindHelper.findOrder(orderId);

		OrderItem orderItem = getItemOrder(orderItem1, order);
		ZonedDateTime nextDelivery = orderItem.getNextDelivery().plusDays(delay);
		orderItem.setNextDelivery(nextDelivery);
		return orderItem;
	}

	public OrderItem updateDeliveryInfo(
		ZonedDateTime paymentDay,
		ZonedDateTime nextDelivery,
		OrderItem orderItem
	) {
		orderItem.setPaymentDay(paymentDay);
		orderItem.setNextDelivery(nextDelivery);
		return orderItem;
	}

	public OrderItem itemOrderCopy(Long lastOrderId, Order newOrder, OrderItem itemOrder) {
		Order order = orderFindHelper.findOrder(lastOrderId);

		OrderItem orderItem = new OrderItem(getItemOrder(itemOrder, order));
		orderItem.setOrder(newOrder);
		return orderItem;
	}

	public void cancelItemOrder(Long orderId, OrderItem orderItem) {
		Order order = orderFindHelper.findOrder(orderId);
		OrderItem orderInOrderItem = getItemOrder(orderItem, order);
		orderInOrderItem.cancelSubscription();
	}

	public void addSalses(List<OrderItem> orderItems) {
		List<Long> orderedItemsId = orderItems.stream()
			.map(orderItem -> orderItem.getItem().getId())
			.collect(Collectors.toUnmodifiableList());

		itemCommandService.addSales(orderedItemsId);
	}

	private Item findItem(long id) {
		return itemFindHelper.findItem(id);
	}

	private OrderItem getItemOrder(OrderItem io, Order order) {
		int i = order.getOrderItems().indexOf(io);
		return order.getOrderItems().get(i);
	}

	private List<OrderItem> getOrderItemSingle(OrderItemServiceDto dto) {
		Item item = findItem(dto.getItemId());

		OrderItem orderItem = OrderItem.create(
			item,
			SubscriptionInfo.of(dto.isSubscription(), dto.getPeriod()),
			dto.getQuantity()
		);

		return Stream.of(orderItem).collect(Collectors.toList());
	}
}