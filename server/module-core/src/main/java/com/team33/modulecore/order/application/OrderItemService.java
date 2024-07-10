package com.team33.modulecore.order.application;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.ItemFindHelper;
import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.item.application.ItemCommandService;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.order.dto.OrderItemServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemService {

	private final ItemCommandService itemCommandService;
	private final OrderFindHelper orderFindHelper;
	private final ItemFindHelper itemFindHelper;
	private final OrderQueryRepository orderQueryRepository;

	@Transactional(readOnly = true)
	public List<OrderItem> toOrderItems(List<OrderItemServiceDto> dtos) {
		return dtos.stream()
			.map(this::makeOrderItem)
			.collect(Collectors.toList());
	}

	public void setItemPeriod(int period, OrderItem orderItem) {
		orderItem.addPeriod(period);
		log.error("주기변경 = {}", orderItem.getPeriod());
	}

	public void updateNextPaymentDate(
		ZonedDateTime paymentDay,
		OrderItem orderItem
	) {
		orderItem.applyNextPaymentDate(paymentDay);
	}
	//
	// public OrderItem itemOrderCopy(Long lastOrderId, Order newOrder, OrderItem itemOrder) {
	// 	Order order = orderFindHelper.findOrder(lastOrderId);
	//
	// 	OrderItem orderItem = new OrderItem(getItemOrder(itemOrder, order));
	// 	// orderItem.setOrder(newOrder);
	// 	return orderItem;
	// }
	//
	// public void cancelItemOrder(Long orderId, OrderItem orderItem) {
	// 	Order order = orderFindHelper.findOrder(orderId);
	// 	OrderItem orderInOrderItem = getItemOrder(orderItem, order);
	// 	orderInOrderItem.cancelSubscription();
	// }


	private Item findItem(long id) {
		return itemFindHelper.findItem(id);
	}

	private OrderItem getItemOrder(OrderItem io, Order order) {
		int i = order.getOrderItems().indexOf(io);
		return order.getOrderItems().get(i);
	}

	private OrderItem makeOrderItem(OrderItemServiceDto dto) {
		Item item = findItem(dto.getItemId());

		return OrderItem.create(
			item,
			SubscriptionInfo.of(dto.isSubscription(), dto.getPeriod()),
			dto.getQuantity()
		);
	}

	public OrderItem findOrderItem(long itemOrderId) {
		return orderQueryRepository.findSubscriptionOrderItemBy(itemOrderId);
	}
}