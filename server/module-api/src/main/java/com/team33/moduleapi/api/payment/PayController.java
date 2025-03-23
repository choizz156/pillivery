package com.team33.moduleapi.api.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.moduleapi.api.payment.dto.KaKaoApproveResponseDto;
import com.team33.moduleapi.api.payment.dto.KaKaoPayNextUrlDto;
import com.team33.moduleapi.api.payment.mapper.PaymentData;
import com.team33.moduleapi.api.payment.mapper.PaymentDataService;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.modulecore.core.order.application.OrderPaymentCodeService;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.payment.application.approve.ApproveFacade;
import com.team33.modulecore.core.payment.application.request.RequestFacade;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/payments")
@RestController
public class PayController {

	private final ApproveFacade<KakaoApproveResponse, KakaoApproveOneTimeRequest> approveFacade;
	private final RequestFacade<KakaoRequestResponse> kakaoRequestFacade;
	private final PaymentMapper paymentMapper;
	private final PaymentDataService paymentDataService;
	private final OrderStatusService orderStatusService;
	private final OrderPaymentCodeService orderPaymentCodeService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{orderId}")
	public SingleResponseDto<KaKaoPayNextUrlDto> request(
		@PathVariable long orderId
	) {

		KakaoRequestResponse requestResponse = kakaoRequestFacade.request(orderId);
		processPaymentData(orderId, requestResponse);

		return new SingleResponseDto<>(KaKaoPayNextUrlDto.from(requestResponse));
	}

	@GetMapping("/approve/subscription/{orderId}")
	public SingleResponseDto<KaKaoApproveResponseDto> approveSubscription(
		@RequestParam("pg_token") String pgToken,
		@PathVariable long orderId
	) {
		PaymentData data = paymentDataService.getData(orderId);
		KakaoApproveOneTimeRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());

		KakaoApproveResponse approve = approveFacade.approveFirst(approveOneTimeRequest);
		orderStatusService.processSubscriptionStatus(orderId, approve.getSid());

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approve));
	}

	@GetMapping("/approve/{orderId}")
	public SingleResponseDto<KaKaoApproveResponseDto> approveOneTime(
		@RequestParam("pg_token") String pgToken,
		@PathVariable long orderId
	) {

		PaymentData data = paymentDataService.getData(orderId);
		KakaoApproveOneTimeRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());

		KakaoApproveResponse approve = approveFacade.approveFirst(approveOneTimeRequest);
		orderStatusService.processOneTimeStatus(orderId);

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approve));
	}

	@PostMapping("/approve/subscriptions/{orderId}")
	public SingleResponseDto<KaKaoApproveResponseDto> subscription(
		@PathVariable long orderId
	) {
		KakaoApproveResponse kaKaoApiApproveResponse = approveFacade.approveSubscription(orderId);

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(kaKaoApiApproveResponse));
	}

	private void processPaymentData(long orderId, KakaoRequestResponse requestResponse) {
		paymentDataService.addData(orderId, requestResponse.getTid());
		orderPaymentCodeService.addTid(orderId, requestResponse.getTid());
	}
}
