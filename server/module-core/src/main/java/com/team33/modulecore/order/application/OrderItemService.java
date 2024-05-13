package com.team33.modulecore.order.application;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.order.dto.OrderItemServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemService {

	private final OrderRepository orderRepository;
	private final ItemQueryRepository itemQueryRepository;

	@Transactional(readOnly = true)
	public List<OrderItem> getOrderItemSingle(OrderItemServiceDto dto) {
		Item item = findItem(dto.getItemId());

		OrderItem orderItem = OrderItem.create(
			item,
			SubscriptionInfo.of(dto.isSubscription(), dto.getPeriod()),
			dto.getQuantity()
		);

		return Stream.of(orderItem).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<OrderItem> getOrderItemsInCart(List<OrderItemServiceDto> dtos) {
		return dtos.stream()
			.map(this::getOrderItemSingle)
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	public void setItemPeriod(int period, OrderItem orderItem) {
		orderItem.addPeriod(period);
		log.error("주기변경 = {}", orderItem.getPeriod());
	}

	public OrderItem delayDelivery(Long orderId, Integer delay, OrderItem io) {

		Optional<Order> order = orderRepository.findById(orderId);

		if (order.isPresent()) {
			OrderItem orderItem = getItemOrder(io, order);
			ZonedDateTime nextDelivery = orderItem.getNextDelivery().plusDays(delay);
			orderItem.setNextDelivery(nextDelivery);
			return orderItem;
		}
		throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
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

	public OrderItem itemOrderCopy(Long lastOrderId, Order newOrder, OrderItem io) {
		Optional<Order> orderEntity = orderRepository.findById(lastOrderId);

		if (orderEntity.isPresent()) {
			OrderItem orderItem = new OrderItem(getItemOrder(io, orderEntity));
			orderItem.setOrder(newOrder);
			return orderItem;
		}
		throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
	}

	public void cancelItemOrder(Long orderId, OrderItem orderItem) {
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isPresent()) {
			OrderItem orderInOrderItem = getItemOrder(orderItem, order);
			orderInOrderItem.cancelSubscription();
			log.warn("is subsucription = {}", orderInOrderItem.isSubscription());
		}
	}

	private Item findItem(long id) {
		return itemQueryRepository.findById(id);
	}

	//TODO: 리팩토링 -> optional 제거
	private OrderItem getItemOrder(OrderItem io, Optional<Order> order) {
		int i = order.get().getOrderItems().indexOf(io);
		return order.get().getOrderItems().get(i);
	}

	public OrderItem findOrderItem(Long itemOrderId) {
		return null;
	}
}