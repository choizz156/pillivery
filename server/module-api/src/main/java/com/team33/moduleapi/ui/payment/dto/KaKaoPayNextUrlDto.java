package com.team33.moduleapi.ui.payment.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KaKaoPayNextUrlDto {
	private String tid;
	private String next_redirect_pc_url;
	private ZonedDateTime createAt;

	@Builder
	private KaKaoPayNextUrlDto(String tid, String next_redirect_pc_url, ZonedDateTime createAt) {
		this.tid = tid;
		this.next_redirect_pc_url = next_redirect_pc_url;
		this.createAt = createAt;
	}

	public static KaKaoPayNextUrlDto from(KakaoRequestResponse requestResponse) {
		return KaKaoPayNextUrlDto.builder()
			.tid(requestResponse.getTid())
			.next_redirect_pc_url(requestResponse.getNext_redirect_pc_url())
			.createAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
			.build();
	}
}
