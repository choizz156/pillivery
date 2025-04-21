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
import com.team33.moduleapi.api.payment.mapper.PaymentDataMapper;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.moduleapi.response.SingleResponseDto;
import com.team33.modulecore.core.order.application.OrderPaymentCodeService;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.payment.domain.request.RequestService;
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
	private final PaymentDataMapper paymentDataMapper;
	private final OrderStatusService orderStatusService;
	private final OrderPaymentCodeService orderPaymentCodeService;
	private final RequestService<KakaoRequestResponse, Long> kakaoRequestService;
	private final RequestService<KakaoRequestResponse, Long> kakaoSubscriptionRequestService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{orderId}")
	public SingleResponseDto<KaKaoPayNextUrlDto> request(
		@PathVariable long orderId
	) {

		KakaoRequestResponse requestResponse = kakaoRequestService.request(orderId);
		processPaymentData(orderId, requestResponse);

		return new SingleResponseDto<>(KaKaoPayNextUrlDto.from(requestResponse));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/subscriptionsFirst/{subscriptionOrderId}")
	public SingleResponseDto<KaKaoPayNextUrlDto> requestSid(
		@PathVariable long subscriptionOrderId
	) {

		KakaoRequestResponse requestResponse = kakaoSubscriptionRequestService.request(subscriptionOrderId);
		processPaymentData(subscriptionOrderId, requestResponse);

		return new SingleResponseDto<>(KaKaoPayNextUrlDto.from(requestResponse));
	}

	@GetMapping("/approve/{orderId}")
	public SingleResponseDto<KaKaoApproveResponseDto> approveOneTime(
		@RequestParam("pg_token") String pgToken,
		@PathVariable Long orderId
	) {

		PaymentData data = paymentDataMapper.getData(orderId);
		KakaoApproveRequest approveOneTimeRequest = paymentMapper.toApproveOneTime(
			data.getTid(), pgToken, data.getTargetId()
		);

		KakaoApproveResponse approve = approveFacade.approveInitially(approveOneTimeRequest);
		orderStatusService.processOneTimeStatus(orderId);

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approve));
	}

	@GetMapping("/approve/subscriptionsFirst/{subscriptionOrderId}")
	public SingleResponseDto<KaKaoApproveResponseDto> approveSubscription(
			@RequestParam("pg_token") String pgToken,
			@PathVariable Long subscriptionOrderId) {

		PaymentData data = paymentDataMapper.getData(subscriptionOrderId);
		KakaoApproveRequest approveSidRequest = paymentMapper.toApproveSubscribe(data.getTid(), pgToken,
				data.getTargetId());

		KakaoApproveResponse approveResponse = approveFacade.approveSid(approveSidRequest);
		orderPaymentCodeService.addSid(subscriptionOrderId, approveResponse.getSid());

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approveResponse));
	}

	@PostMapping("/approve/subscriptions/{subscriptionOrderId}")
	public SingleResponseDto<KaKaoApproveResponseDto> subscription(
			@PathVariable Long subscriptionOrderId) {

		KakaoApproveResponse kaKaoApiApproveResponse = approveFacade.approveSubscription(subscriptionOrderId);

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(kaKaoApiApproveResponse));
	}

	private void processPaymentData(long targetId, KakaoRequestResponse requestResponse) {

		paymentDataMapper.addData(targetId, requestResponse.getTid());
		orderPaymentCodeService.addTid(targetId, requestResponse.getTid());
	}
}
