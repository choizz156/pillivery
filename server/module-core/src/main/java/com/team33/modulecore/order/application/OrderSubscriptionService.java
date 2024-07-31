package com.team33.modulecore.order.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.entity.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderSubscriptionService {

	private final OrderFindHelper orderFindHelper;

	public void changeSubscriptionItemQuantity(long orderId, long orderItemId, int quantity) {
		Order findOrder = orderFindHelper.findOrder(orderId);

		List<OrderItem> orderItems = findOrder.getOrderItems();

		changeItemQuantity(orderItemId, quantity, orderItems);

		findOrder.adjustPriceAndTotalQuantity(orderItems);
	}

	private void changeItemQuantity(long orderItemId, int quantity, List<OrderItem> orderItems) {
		orderItems.stream()
			.filter(orderItem -> orderItem.getId().equals(orderItemId))
			.findFirst()
			.ifPresentOrElse(
				orderItem -> orderItem.changeQuantity(quantity),
				() -> {
					throw new BusinessLogicException(ExceptionCode.NOT_ORDERED_ITEM);
				}
			);
	}
}
