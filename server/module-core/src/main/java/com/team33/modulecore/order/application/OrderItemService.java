package com.team33.modulecore.order.application;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.cart.domain.entity.NormalCart;
import com.team33.modulecore.cart.domain.ItemId;
import com.team33.modulecore.cart.repository.NormalCartRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.cart.domain.NormalCartItem;
import com.team33.modulecore.itemcart.repository.ItemCartRepository;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;
import com.team33.modulecore.order.domain.repository.OrderItemRepository;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.order.dto.OrderItemServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemService {

	private final ItemCartRepository itemCartRepository;
	private final OrderItemRepository orderItemRepository;
	private final ItemCommandRepository itemCommandRepository;
	private final OrderRepository orderRepository;
	private final NormalCartRepository normalCartRepository;
	private final ItemQueryRepository itemQueryRepository;

	@Transactional(readOnly = true)
	public List<OrderItem> getOrderItemSingle(OrderItemServiceDto dto) {
		Item item = findItem(dto.getItemId());

		SubscriptionInfo subscriptionInfo =
			SubscriptionInfo.of(dto.isSubscription(), dto.getPeriod());

		OrderItem orderItem = OrderItem.create(
			item,
			subscriptionInfo,
			dto.getQuantity()
		);

		return makeOrderItems(orderItem);
	}

	public List<OrderItem> getOrderItemsInCart1(Long cartId) {
		NormalCart normalCart = normalCartRepository.findById(cartId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

		List<ItemId> itemIds = normalCart.getItemIds();
		List<Item> items = itemIds.stream()
			.map(itemId -> itemCommandRepository.findById(itemId.getId())
				.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND))
			)
			.collect(Collectors.toList());

		List<OrderItem> orderItemList = new ArrayList<>();

		items.stream().map(item -> {
			createOrderItem1(item);
			return
		});

		return orderItemList;
	}

	private OrderItem createOrderItem1(Item item) {
		OrderItem.create(item,,)
	}

	public List<OrderItem> getOrderItemsInCart(List<NormalCartItem> normalCartItems) {
		List<OrderItem> orderItemList = new ArrayList<>();
		normalCartItems.forEach(itemCart -> {
			createOrderItem(itemCart, orderItemList);
			itemCartRepository.deleteById(itemCart.getItemCartId());
		});
		return orderItemList;
	}

	private void createOrderItem(NormalCartItem normalCartItem, List<OrderItem> orderItemList) {
		OrderItem orderItem = OrderItem.create(
			normalCartItem.getItem(),
			normalCartItem.getSubscriptionItemInfo(),
			normalCartItem.getTotalQuantity()
		);
		orderItemList.add(orderItem);
	}

	public OrderItem findOrderItem(long orderItemId) {
		Optional<OrderItem> optionalItemOrder = orderItemRepository.findById(orderItemId);

		return optionalItemOrder.orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND)
		);
	}

	//    public OrderItem changeSubQuantity(long itemOrderId, int upDown) {
	//        OrderItem orderItem = findItemOrder(itemOrderId);
	//
	//
	//        orderItem.setQuantity(orderItem.getQuantity() + upDown);
	//        orderItemRepository.save(orderItem);
	//
	//        return orderItem;
	//    }

	//    public void addSales(OrderItem orderItem) { // 주문 요청할 경우 아이템 판매량 증가
	//        int sales = orderItem.getQuantity();
	//
	//        itemRepository.save(orderItem.getItem());
	//    }

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
			//            addSales(orderItem);
			orderItemRepository.save(orderItem);
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

	private List<OrderItem> makeOrderItems(OrderItem orderItem) {
		List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(orderItem);
		return orderItems;
	}

	private Item findItem(long id) {
		Optional<Item> optionalItem = itemCommandRepository.findById(id);
		return optionalItem.orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND)
		);
	}

	//TODO: 리팩토링 -> optional 제거
	private OrderItem getItemOrder(OrderItem io, Optional<Order> order) {
		int i = order.get().getOrderItems().indexOf(io);
		return order.get().getOrderItems().get(i);
	}

}