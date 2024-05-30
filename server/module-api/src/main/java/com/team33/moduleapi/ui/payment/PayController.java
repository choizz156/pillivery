package com.team33.moduleapi.ui.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleapi.dto.SingleResponseDto;
import com.team33.moduleapi.ui.payment.dto.KaKaoApproveResponseDto;
import com.team33.moduleapi.ui.payment.dto.KaKaoPayNextUrlDto;
import com.team33.moduleapi.ui.payment.mapper.PaymentData;
import com.team33.moduleapi.ui.payment.mapper.PaymentDataService;
import com.team33.moduleapi.ui.payment.mapper.PaymentMapper;
import com.team33.modulecore.order.application.OrderPaymentCodeService;
import com.team33.modulecore.order.application.OrderStatusService;
import com.team33.modulecore.payment.application.approve.ApproveFacade;
import com.team33.modulecore.payment.application.request.RequestFacade;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PayController {

	private final ApproveFacade<KakaoApproveResponse, KakaoApproveOneTimeRequest> approveFacade;
	private final RequestFacade<KakaoRequestResponse> KakaoRequestFacade;
	private final PaymentMapper paymentMapper;
	private final PaymentDataService paymentDataService;
	private final OrderStatusService orderStatusService;
	private final OrderPaymentCodeService paymentCodeService;

	@PostMapping("/{orderId}")
	public SingleResponseDto<?> request(
		@PathVariable Long orderId
	) {

		KakaoRequestResponse requestResponse = KakaoRequestFacade.request(orderId);
		paymentDataService.addData(orderId, requestResponse.getTid());
		paymentCodeService.addTid(orderId, requestResponse.getTid());

		return new SingleResponseDto<>(KaKaoPayNextUrlDto.from(requestResponse));
	}

	@GetMapping("/approve/subscription/{orderId}")
	public SingleResponseDto<?> subscription(
		@RequestParam("pg_token") String pgToken,
		@PathVariable Long orderId
	) {
		PaymentData data = paymentDataService.getData(orderId);
		KakaoApproveOneTimeRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());

		KakaoApproveResponse approve = approveFacade.approveFirst(approveOneTimeRequest);
		orderStatusService.processSubscriptionStatus(orderId, approve.getSid());

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approve));
	}

	@GetMapping("/approve/{orderId}")
	public SingleResponseDto<?> success(
		@RequestParam("pg_token") String pgToken,
		@PathVariable Long orderId
	) {

		PaymentData data = paymentDataService.getData(orderId);
		KakaoApproveOneTimeRequest approveOneTimeRequest =
			paymentMapper.toApproveOneTime(data.getTid(), pgToken, data.getOrderId());

		KakaoApproveResponse approve = approveFacade.approveFirst(approveOneTimeRequest);
		orderStatusService.processOneTimeStatus(orderId);

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(approve));
	}

	@GetMapping("/approve/subscription")
	public SingleResponseDto<?> subscription(
		@RequestParam("orderId") Long orderId
	) {

		KakaoApproveResponse kaKaoApiApproveResponse = approveFacade.approveSubscription(orderId);

		return new SingleResponseDto<>(KaKaoApproveResponseDto.from(kaKaoApiApproveResponse));
	}

	// @GetMapping("/cancel")
	// @ResponseStatus(HttpStatus.BAD_REQUEST)
	// public void cancel() {
	// }

	// @GetMapping("/fail")
	// @ResponseStatus(HttpStatus.BAD_REQUEST)
	// public void fail() {
	// }
}
