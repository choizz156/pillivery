package com.team33.modulecore.payment.kakao.dto;

import com.team33.modulecore.payment.dto.ApproveRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoApproveOneTimeRequest implements ApproveRequest {

	private final String tid;
	private final String pgtoken;
	private final long orderId;

	@Builder
	private KakaoApproveOneTimeRequest(String tid, String pgtoken, long orderId) {
		this.tid = tid;
		this.pgtoken = pgtoken;
		this.orderId = orderId;
	}

	public static KakaoApproveOneTimeRequest of(String tid, String pgtoken, long orderId) {
		return KakaoApproveOneTimeRequest.builder()
			.tid(tid)
			.pgtoken(pgtoken)
			.orderId(orderId)
			.build();
	}
}
