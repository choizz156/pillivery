package com.team33.moduleapi.api.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.api.payment.dto.KaKaoApproveResponseDto;
import com.team33.moduleapi.api.payment.dto.KaKaoPayNextUrlDto;
import com.team33.moduleapi.api.payment.mapper.PaymentData;
import com.team33.moduleapi.api.payment.mapper.PaymentDataService;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.modulecore.core.order.application.OrderPaymentCodeService;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.payment.domain.request.RequestService1;
import com.team33.modulecore.core.payment.kakao.application.approve.KakaoApproveFacade;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/payments")
@RestController
public class PayController {

	private final KakaoApproveFacade approveFacade;
	private final PaymentMapper paymentMapper;
	private final PaymentDataService paymentDataService;
	private final OrderStatusService orderStatusService;
	private final OrderPaymentCodeService orderPaymentCodeService;
	   private final RequestService1<KakaoRequestResponse, Order> kakaoRequestService1;
	private final RequestService1<KakaoRequestResponse, SubscriptionOrder> kakaoSubscriptionRequestService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{orderId}")
	public SingleResponseDto<KaKaoPayNextUrlDto> request(
		@PathVariable long orderId
	) {

		KakaoRequestResponse requestResponse = kakaoRequestService1.request(orderId);
		processPaymentData(orderId, requestResponse);

		return new SingleResponseDto<>(KaKaoPayNextUrlDto.from(requestResponse));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/subscriptions/{subscriptionOrderId}")
	public SingleResponseDto<KaKaoPayNextUrlDto> requestSid(
		@PathVariable long subscriptionOrderId
	) {
		// SubscriptionOrder 조회 로직이 필요할 수 있음
		KakaoRequestResponse requestResponse = kakaoSubscriptionRequestService.request(subscriptionOrderId);
		processPaymentData(subscriptionOrderId, requestResponse);
	
		return new SingleResponseDto<>(KaKaoPayNextUrlDto.from(requestResponse));
	}

	@GetMapping("/approve/subscriptions/{subscriptionOrderId}")
	public SingleResponseDto<KaKaoApproveResponseDto> approveSubscription(
		@RequestParam("pg_token") String pgToken,
		@PathVariable Long subscriptionOrderId
	) {
		PaymentData data = paymentDataService.getData(subscriptionOrderId);
		KakaoApproveRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());

		KakaoApproveResponse approveResponse = approveFacade.approveInitially(approveOneTimeRequest);
		orderPaymentCodeService.addSid(subscriptionOrderId, approveResponse.getSid());

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approveResponse));
	}

	@GetMapping("/approve/{orderId}")
	public SingleResponseDto<KaKaoApproveResponseDto> approveOneTime(
		@RequestParam("pg_token") String pgToken,
		@PathVariable long orderId
	) {

		PaymentData data = paymentDataService.getData(orderId);
		KakaoApproveRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());

		KakaoApproveResponse approve = approveFacade.approveInitially(approveOneTimeRequest);
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
