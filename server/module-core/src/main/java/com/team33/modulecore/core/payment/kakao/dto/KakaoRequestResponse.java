package com.team33.modulecore.core.payment.kakao.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KakaoRequestResponse {

	private String tid;
	private String next_redirect_pc_url;

	@Builder
	public KakaoRequestResponse(String tid, String next_redirect_pc_url) {
		this.tid = tid;
		this.next_redirect_pc_url = next_redirect_pc_url;
	}
}
