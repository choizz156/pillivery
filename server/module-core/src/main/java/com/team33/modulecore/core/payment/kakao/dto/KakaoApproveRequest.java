package com.team33.modulecore.core.payment.kakao.dto;

import com.team33.modulecore.core.payment.dto.ApproveRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoApproveRequest implements ApproveRequest {

	private final String tid;
	private final String pgtoken;
	private final Long orderId;
	private final Long subscriptionOrderId;

	@Builder
	private KakaoApproveRequest(String tid, String pgtoken, long orderId, Long subscriptionOrderId) {
		this.tid = tid;
		this.pgtoken = pgtoken;
		this.orderId = orderId;
		this.subscriptionOrderId = subscriptionOrderId;
	}

	public static KakaoApproveRequest of(String tid, String pgtoken, Long orderId) {
		return KakaoApproveRequest.builder()
			.tid(tid)
			.pgtoken(pgtoken)
			.orderId(orderId)
			.build();
	}

	public static KakaoApproveRequest toSubscriptionRequest(String tid, String pgtoken, Long subscriptionOrderId) {

		return KakaoApproveRequest.builder()
			.tid(tid)
			.pgtoken(pgtoken)
			.subscriptionOrderId(subscriptionOrderId)
			.build();
	}
}
