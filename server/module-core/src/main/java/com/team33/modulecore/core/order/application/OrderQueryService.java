package com.team33.modulecore.core.order.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPageRequest;
import com.team33.modulecore.core.order.dto.OrderQueryDto;
import com.team33.modulecore.core.order.dto.query.OrderItemQueryDto;
import com.team33.modulecore.core.order.dto.query.SubscriptionOrderItemQueryDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderQueryService {

	private final OrderQueryRepository orderQueryRepository;

	public Page<OrderItemQueryDto> findAllOrders(long userId, OrderPageRequest orderPageRequest) {

		return orderQueryRepository.findOrdersWithItems(
			orderPageRequest,
			OrderFindCondition.to(userId, OrderStatus.COMPLETE)
		);
	}

	public Page<SubscriptionOrderItemQueryDto> findAllSubscriptions(
			Long userId,
			OrderPageRequest orderPageRequest
	) {
		return orderQueryRepository.findSubscriptionOrderItemsWithItems(
				orderPageRequest,
				OrderFindCondition.to(userId, OrderStatus.SUBSCRIPTION)
		);
	}

	public List<OrderQueryDto> findOrder(Long orderId) {
		return orderQueryRepository.findById(orderId);
	}
}
