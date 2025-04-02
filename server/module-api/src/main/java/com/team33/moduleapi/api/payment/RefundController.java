package com.team33.moduleapi.api.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.api.payment.dto.RefundDto;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.payment.domain.refund.RefundService;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments/refund")
public class RefundController {

	private final PaymentMapper paymentMapper;
	private final RefundService refundService;
	private final OrderStatusService orderStatusService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{orderId}")
	public SingleResponseDto<String> refund(
		@PathVariable Long orderId,
		@RequestBody RefundDto refundDto
	) {

		RefundContext refundContext = paymentMapper.toRefundContext(refundDto);
		refundService.refund(orderId, refundContext);
		orderStatusService.processCancel(orderId);

		return new SingleResponseDto<>("complete");
	}
}
