package com.team33.moduleexternalapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoRequestResponse {

	private String tid;
	private String next_redirect_pc_url;
}
