package com.team33.modulecore.core.payment.kakao.dto;

import com.team33.modulecore.core.payment.dto.ApproveRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoApproveRequest implements ApproveRequest {

	private final String tid;
	private final String pgtoken;
	private final long orderId;

	@Builder
	private KakaoApproveRequest(String tid, String pgtoken, long orderId) {
		this.tid = tid;
		this.pgtoken = pgtoken;
		this.orderId = orderId;
	}

	public static KakaoApproveRequest of(String tid, String pgtoken, long orderId) {
		return KakaoApproveRequest.builder()
			.tid(tid)
			.pgtoken(pgtoken)
			.orderId(orderId)
			.build();
	}
}
