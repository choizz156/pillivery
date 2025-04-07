package com.team33.modulecore.core.order.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPageRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

		return orderQueryRepository.findSubscriptionOrderItems(
			orderPageRequest,
			OrderFindCondition.to(userId, OrderStatus.SUBSCRIPTION)
		);
	}

	public Order findOrder(Long orderId) {
		return orderQueryRepository.findById(orderId);
	}
}
