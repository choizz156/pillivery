package com.team33.modulecore.payment.kakao.application.request;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.payment.application.NormalRequest;
import com.team33.modulecore.payment.application.RequestFacade;
import com.team33.modulecore.payment.application.SubscriptionRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoRequestFacade implements RequestFacade<KakaoRequestResponse> {

	private final OrderRepository orderRepository;
	private final NormalRequest<KakaoRequestResponse> normalRequest;
	private final SubscriptionRequest<KakaoRequestResponse> subscriptionRequest;

	@Override
	public KakaoRequestResponse request(long orderId) {
		Order order = findOrder(orderId);

		if(order.isSubscription()){
			return subscriptionRequest.requestSubscription(order);
		}

		KakaoRequestResponse request = normalRequest.requestOneTime(order);
		return request;
	}

	private Order findOrder(Long orderId) {
		Optional<Order> orderOptional = orderRepository.findById(orderId);
		return orderOptional.orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND)
		);
	}
}
