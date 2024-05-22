package com.team33.modulecore.payment.kakao.application;

import static com.team33.modulecore.payment.kakao.application.ParamsConst.*;

import org.springframework.stereotype.Service;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.payment.application.refund.RefundContext;
import com.team33.modulecore.payment.application.refund.RefundService;
import com.team33.moduleexternalapi.domain.RefundClient;
import com.team33.moduleexternalapi.infra.RefundParams;
import com.team33.moduleexternalapi.dto.KakaoRefundResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoRefundService implements RefundService<KakaoRefundResponse> {

	private static final String REFUND_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";

	private final RefundClient<KakaoRefundResponse> kakaoRefundClient;
	private final ParameterProvider parameterProvider;
	private final OrderFindHelper orderFindHelper;

	@Override
	public KakaoRefundResponse refund(RefundContext refundContext) {

		String tid = orderFindHelper.findTid(refundContext.getOrderId());

		RefundParams refundParam = RefundParams.builder()
			.cancelAmount(refundContext.getCancelAmount())
			.cancelTaxFreeAmount(refundContext.getCancelTaxFreeAmount())
			.tid(tid)
			.cid(ONE_TIME_CID)
			.build();

		return kakaoRefundClient.send(refundParam, REFUND_URL);
	}

}
