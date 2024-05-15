package com.team33.modulecore.payment.kakao.dto;

import com.team33.modulecore.payment.dto.ApproveRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoApproveOneTimeRequest implements ApproveRequest {

	private String tid;
	private String pgtoken;
	private Long orderId;

	@Builder
	private KakaoApproveOneTimeRequest(String tid, String pgtoken, Long orderId) {
		this.tid = tid;
		this.pgtoken = pgtoken;
		this.orderId = orderId;
	}

	public static KakaoApproveOneTimeRequest of(String tid, String pgtoken, Long orderId) {
		return KakaoApproveOneTimeRequest.builder()
			.tid(tid)
			.pgtoken(pgtoken)
			.orderId(orderId)
			.build();
	}
}
