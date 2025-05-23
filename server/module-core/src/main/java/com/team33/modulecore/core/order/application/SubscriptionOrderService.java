package com.team33.modulecore.core.order.application;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.item.event.ItemSaleCountedEvent;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.SubscriptionOrderRepository;
import com.team33.modulecore.core.payment.kakao.application.events.PaymentDateUpdatedEvent;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionOrderService {

	private final SubscriptionOrderRepository subscriptionOrderRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	public List<SubscriptionOrder> create(Order order) {

		List<SubscriptionOrder> subscriptionOrders = order.getOrderItems()
			.stream()
			.map(orderItem -> SubscriptionOrder.create(order, orderItem))
			.collect(Collectors.toUnmodifiableList());

		return subscriptionOrderRepository.saveAll(subscriptionOrders);
	}

	public void publishSubscriptionApproveEvent(SubscriptionOrder subscriptionOrder){
		applicationEventPublisher.publishEvent(new PaymentDateUpdatedEvent(subscriptionOrder.getId()));
		applicationEventPublisher.publishEvent(new ItemSaleCountedEvent(subscriptionOrder.getItemId()));
	}

	public void changeItemPeriod(int newPeriod, long itemOrderId) {
		findById(itemOrderId).changePeriod(newPeriod);
	}

	public SubscriptionOrder findById(long subscriptionOrderId) {
		return subscriptionOrderRepository.findById(subscriptionOrderId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
	}

	public void updateNextPaymentDate(
		ZonedDateTime paymentDay,
		long subscriptionOrderId
	) {
		SubscriptionOrder subscriptionOrder = findById(subscriptionOrderId);

		subscriptionOrder.updateSubscriptionPaymentDay(paymentDay);
	}

	public void cancelSubscription(long subscriptionOrderId) {
		findById(subscriptionOrderId).cancelSubscription();
	}

	public void updateOrderStatusToFail(long subscriptionOrderId) {
		findById(subscriptionOrderId).changeOrderStatus(OrderStatus.SUBSCRIBE_PAYMENT_FAIL);
	}
}
