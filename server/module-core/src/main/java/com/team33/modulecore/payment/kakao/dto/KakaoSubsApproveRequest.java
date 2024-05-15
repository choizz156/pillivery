package com.team33.modulecore.payment.kakao.dto;

import com.team33.modulecore.payment.dto.ApproveRequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoSubsApproveRequest implements ApproveRequest {
	private String sid;
	private Long orderId;

	@Builder
	private KakaoSubsApproveRequest(String sid, Long orderId) {
		this.sid = sid;
		this.orderId = orderId;
	}

	public static KakaoSubsApproveRequest of(String sid, Long orderId) {
		return KakaoSubsApproveRequest.builder()
			.sid(sid)
			.orderId(orderId)
			.build();
	}
}
