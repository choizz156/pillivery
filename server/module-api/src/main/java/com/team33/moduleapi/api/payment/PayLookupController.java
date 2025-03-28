package com.team33.moduleapi.api.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.domain.request.RequestService;
import com.team33.modulecore.core.payment.kakao.dto.KakaoLookupResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments/lookup")
public class PayLookupController {

	private final RequestService<KakaoLookupResponse, Order> kakaoPayLookupService;
	private final OrderFindHelper orderFindHelper;

	@GetMapping("/{orderId}")
	public SingleResponseDto<KakaoLookupResponse> findOrder(
		@PathVariable Long orderId
	) {

		Order order = orderFindHelper.findOrder(orderId);
		KakaoLookupResponse response = kakaoPayLookupService.request(order);

		return new SingleResponseDto<>(response);
	}
}
