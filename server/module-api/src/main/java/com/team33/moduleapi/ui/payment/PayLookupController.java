package com.team33.moduleapi.ui.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.request.RequestService;
import com.team33.modulecore.payment.kakao.dto.KakaoLookupResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments/lookup")
public class PayLookupController {

	private final RequestService<KakaoLookupResponse> kakaoPayLookupService;
	private final OrderFindHelper orderFindHelper;

	@GetMapping("/{orderId}")
	public SingleResponseDto<?> findOrder(
		@PathVariable Long orderId
	) {

		Order order = orderFindHelper.findOrder(orderId);
		KakaoLookupResponse response = kakaoPayLookupService.request(order);
		return new SingleResponseDto<>(response);
	}
}
