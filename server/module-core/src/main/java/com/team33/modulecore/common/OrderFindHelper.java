package com.team33.modulecore.common;

import org.springframework.stereotype.Component;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderFindHelper {

	private final OrderRepository orderRepository;

	public Order findOrder(Long id) {
		return orderRepository.findById(id)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
	}

	public boolean checkSubscription(Long orderId){
		return orderRepository.findIsSubscriptionById(orderId);
	}

	public String findTid(Long orderId) {
		String tid = orderRepository.findTid(orderId);
		if(tid == null){
			throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
		}
		return tid;
	}
}
