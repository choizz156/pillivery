package com.team33.modulecore.order.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderQueryService {

	private final OrderQueryRepository orderQueryRepository;

	public Page<Order> findAllOrders(long userId, OrderPageRequest orderPageRequest) {

		return orderQueryRepository.findOrders(
			orderPageRequest,
			OrderFindCondition.to(userId, OrderStatus.REQUEST)
		);
	}

	public List<OrderItem> findAllSubscriptions(
		Long userId,
		OrderPageRequest orderPageRequest
	) {

		return orderQueryRepository.findSubscriptionOrderItem(
			orderPageRequest,
			OrderFindCondition.to(userId, OrderStatus.SUBSCRIBE)
		);
	}

	public Order findOrder(Long orderId) {
		return orderQueryRepository.findById(orderId);
	}
}
