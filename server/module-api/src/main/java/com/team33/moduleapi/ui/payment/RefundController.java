package com.team33.moduleapi.ui.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.payment.dto.RefundDto;
import com.team33.moduleapi.ui.payment.mapper.PaymentMapper;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments/refund")
public class RefundController {

	private final PaymentMapper paymentMapper;
	private final OrderStatusService orderStatusService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{orderId}")
	public SingleResponseDto<?> refund(
		@PathVariable Long orderId,
		@RequestBody RefundDto refundDto
	) {

		RefundContext refundContext = paymentMapper.toRefundContext(refundDto, orderId);
		orderStatusService.processCancel(orderId , refundContext);

		return new SingleResponseDto<>("complete");
	}
}
