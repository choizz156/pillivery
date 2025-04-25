package com.team33.modulecore.core.common;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderFindHelper {

	private final OrderCommandRepository orderCommandRepository;
	private final OrderQueryRepository orderQueryRepository;

	public Order findOrder(Long id) {
		return orderCommandRepository.findById(id)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
	}

	public boolean checkSubscription(Long orderId){
		return orderQueryRepository.findIsSubscriptionById(orderId);
	}

	public String findTid(Long orderId) {
		String tid = orderQueryRepository.findTid(orderId);
		if(tid == null){
			throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
		}
		return tid;
	}
}
