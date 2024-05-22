package com.team33.modulecore.payment.kakao.application;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.payment.application.refund.RefundContext;
import com.team33.modulecore.payment.application.refund.RefundService;
import com.team33.moduleexternalapi.domain.PaymentClient;
import com.team33.moduleexternalapi.dto.KakaoRefundResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoRefundService implements RefundService<KakaoRefundResponse> {

	private static final String REFUND_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";

	private final PaymentClient<KakaoRefundResponse> kakaoRefundClient;
	private final ParameterProvider parameterProvider;
	private final OrderFindHelper orderFindHelper;

	@Override
	public KakaoRefundResponse refund(RefundContext refundContext) {

		String tid = orderFindHelper.findTid(refundContext.getOrderId());

		Map<String, String> refundParams =
			parameterProvider.getRefundParams(
				tid,
				refundContext.getCancelAmount(),
				refundContext.getCancelTaxFreeAmount()
			);

		return kakaoRefundClient.send(refundParams, REFUND_URL);
	}
}
