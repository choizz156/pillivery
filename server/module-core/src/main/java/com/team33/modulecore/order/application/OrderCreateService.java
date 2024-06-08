package com.team33.modulecore.order.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.order.dto.OrderContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderCreateService {

	private final OrderCommandRepository orderCommandRepository;
	private final UserFindHelper userFindHelper;

	public Order callOrder(List<OrderItem> orderItems, OrderContext orderContext) {
		Order order = createOrder(orderItems, orderContext);
		return orderCommandRepository.save(order);
	}

	public Order deepCopy(Order order) {
		Order newOrder = new Order(order);
		orderCommandRepository.save(newOrder);

		return newOrder;
	}

	public Order findOrder(long orderId) {
		return orderCommandRepository.findById(orderId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
	}

	private Order createOrder(List<OrderItem> orderItems, OrderContext orderContext) {

		return Order.create(orderItems, orderContext);
	}
}
