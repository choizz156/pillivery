package com.team33.modulecore.core.order.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.order.domain.OrderCommonInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderCreateService {

	private final OrderCommandRepository orderCommandRepository;

	public Order callOrder(List<OrderItem> orderItems, OrderContext orderContext) {
		Order order = createOrder(orderItems, orderContext);
		return orderCommandRepository.save(order);
	}

	public Order findOrder(long orderId) {
		return orderCommandRepository.findById(orderId)
				.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
	}

	private Order createOrder(List<OrderItem> orderItems, OrderContext orderContext) {
		OrderCommonInfo commonInfo = OrderCommonInfo.createFromContext(orderItems, orderContext);
				
		return Order.create(orderItems, commonInfo, orderContext);
	}
}
