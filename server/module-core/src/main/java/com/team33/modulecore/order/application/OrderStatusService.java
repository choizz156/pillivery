package com.team33.modulecore.order.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.events.CartRefreshedEvent;
import com.team33.modulecore.order.events.ItemSaleCountedEvent;
import com.team33.modulecore.payment.application.refund.RefundService;
import com.team33.modulecore.payment.kakao.application.refund.RefundContext;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderStatusService {

	private final ApplicationContext applicationContext;
	private final OrderFindHelper orderFindHelper;
	private final OrderPaymentCodeService orderPaymentCodeService;
	private final RefundService refundService;

	public void processOneTimeStatus(Long orderId) {

		Order order = orderFindHelper.findOrder(orderId);

		order.changeOrderStatus(OrderStatus.COMPLETE);

		List<Long> orderedItemsId = getOrderedIds(order);

		applicationContext.publishEvent(new ItemSaleCountedEvent(orderedItemsId));
		applicationContext.publishEvent(new CartRefreshedEvent(order, orderedItemsId));
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processSubscriptionStatus(Long orderId, String sid) {

		Order order = orderFindHelper.findOrder(orderId);

		order.changeOrderStatus(OrderStatus.SUBSCRIBE);
		List<Long> orderedItemsId = getOrderedIds(order);

		orderPaymentCodeService.addSid(order, sid);

		applicationContext.publishEvent(new ItemSaleCountedEvent(orderedItemsId));
		applicationContext.publishEvent(new CartRefreshedEvent(order, orderedItemsId));
	}

	public void processCancel(Long orderId, RefundContext refundContext) {
		Order order = orderFindHelper.findOrder(orderId);
		order.changeOrderStatus(OrderStatus.Refund);
		refundService.refund(refundContext);
	}

	private List<Long> getOrderedIds(Order order) {
		return order.getOrderItems()
			.stream()
			.map(orderItem -> orderItem.getItem().getId())
			.collect(Collectors.toUnmodifiableList());
	}

	public void processSubscriptionCancel(Long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		order.changeOrderStatus(OrderStatus.SUBSCRIBE_CANCEL);
	}
}
