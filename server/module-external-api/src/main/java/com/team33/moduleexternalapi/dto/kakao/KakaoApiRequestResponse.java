package com.team33.moduleexternalapi.dto.kakao;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoApiRequestResponse {

	private String tid;
	private String next_redirect_pc_url;

	@Builder
	public KakaoApiRequestResponse(String tid, String next_redirect_pc_url) {
		this.tid = tid;
		this.next_redirect_pc_url = next_redirect_pc_url;
	}
}
