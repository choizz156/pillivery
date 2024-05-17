package com.team33.modulecore.order.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.user.domain.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderQueryService {

	private final OrderQueryRepository orderQueryRepository;
	private final UserFindHelper userFindHelper;

	public Page<Order> findAllOrders(Long userId, OrderPageRequest orderPageRequest) {
		User user = userFindHelper.findUser(userId);

		return orderQueryRepository.findOrders(
			orderPageRequest,
			OrderFindCondition.to(user, OrderStatus.REQUEST)
		);
	}

	public List<OrderItem> findAllSubscriptions(
		Long userId,
		OrderPageRequest orderPageRequest
	) {
		User user = userFindHelper.findUser(userId);

		return orderQueryRepository.findSubscriptionOrderItem(
			orderPageRequest,
			OrderFindCondition.to(user, OrderStatus.SUBSCRIBE)
		);
	}

	public Order findOrder(Long orderId) {
		return orderQueryRepository.findById(orderId);
	}
}
