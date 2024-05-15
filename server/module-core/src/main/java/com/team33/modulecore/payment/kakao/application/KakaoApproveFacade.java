package com.team33.modulecore.payment.kakao.application;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.payment.application.NormalApprove;
import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoApproveFacade implements
	com.team33.modulecore.payment.application.ApproveFacade<KaKaoApproveResponse, KakaoApproveOneTimeRequest> {

	private final OrderRepository orderRepository;
	private final OrderService orderService;
	private final SubsFirstApproveService subsFirstApproveService;
	private final NormalApprove<KaKaoApproveResponse, KakaoApproveOneTimeRequest> normalApprove;

	@Override
	public KaKaoApproveResponse approve(KakaoApproveOneTimeRequest approveRequest) {
		Long orderId = approveRequest.getOrderId();
		Order order = findOrder(approveRequest.getOrderId());

		if (order.isSubscription()) {
			KaKaoApproveResponse approve =
				subsFirstApproveService.approveFirstSubscription(approveRequest);

			order.addSid(approve.getSid());
			orderService.changeOrderStatusToSubscribe(orderId);
			// doKakaoScheduling(orderId);
			return approve;
		}

		KaKaoApproveResponse approve = normalApprove.approveOneTime(approveRequest);
		orderService.changeOrderStatusToComplete(orderId);
		return approve;
	}

	private Order findOrder(Long orderId) {
		Optional<Order> orderOptional = orderRepository.findById(orderId);
		return orderOptional.orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND)
		);
	}
}