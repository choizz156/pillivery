package com.team33.modulecore.core.order.application;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.events.CartRefreshedEvent;
import com.team33.modulecore.core.order.events.ItemSaleCountedEvent;
import com.team33.modulecore.core.payment.domain.cancel.CancelSubscriptionService;
import com.team33.modulecore.core.payment.domain.refund.RefundService;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class OrderStatusService {

	private final ApplicationContext applicationContext;
	private final OrderFindHelper orderFindHelper;
	private final OrderPaymentCodeService orderPaymentCodeService;
	private final CancelSubscriptionService kakaoSubsCancelService;
	private final RefundService refundService;

	public void processOneTimeStatus(Long orderId) {

		Order order = orderFindHelper.findOrder(orderId);

		order.changeOrderStatus(OrderStatus.COMPLETE);

		List<Long> orderedItemsId = getOrderedIds(order);

		applicationContext.publishEvent(new ItemSaleCountedEvent(orderedItemsId));
		applicationContext.publishEvent(new CartRefreshedEvent(order, orderedItemsId));
	}

	@Transactional
	public void processSubscriptionStatus(Long orderId, String sid) {

		Order order = orderFindHelper.findOrder(orderId);

		order.changeOrderStatus(OrderStatus.SUBSCRIBE);
		// order.getOrderItems()
			// .forEach(orderItem -> orderItem.updateSubscriptionPaymentDay(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))));
		List<Long> orderedItemsId = getOrderedIds(order);

		orderPaymentCodeService.addSid(orderId, sid);

		applicationContext.publishEvent(new ItemSaleCountedEvent(orderedItemsId));
		applicationContext.publishEvent(new CartRefreshedEvent(order, orderedItemsId));
	}

	public void processCancel(Long orderId, RefundContext refundContext) {
		Order order = orderFindHelper.findOrder(orderId);

		order.changeOrderStatus(OrderStatus.REFUND);

		refundService.refund(orderId, refundContext);
	}

	public void processSubscriptionCancel(long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		order.changeOrderStatus(OrderStatus.SUBSCRIBE_CANCEL);

		kakaoSubsCancelService.cancelSubscription(order);
	}

	private List<Long> getOrderedIds(Order order) {
		return order.getOrderItems()
			.stream()
			.map(orderItem -> orderItem.getItem().getId())
			.collect(Collectors.toUnmodifiableList());
	}
}
