package com.team33.moduleapi.api.payment.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KaKaoPayNextUrlDto {
	private String tid;
	private String next_redirect_pc_url;

	@JsonProperty("createdAt")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy‑MM‑dd'T'HH:mm:ssXXX")
	private ZonedDateTime createdAt;

	@Builder
	private KaKaoPayNextUrlDto(String tid, String next_redirect_pc_url, ZonedDateTime createdAt) {
		this.tid = tid;
		this.next_redirect_pc_url = next_redirect_pc_url;
		this.createdAt = createdAt;
	}

	public static KaKaoPayNextUrlDto from(KakaoRequestResponse requestResponse) {
		return KaKaoPayNextUrlDto.builder()
			.tid(requestResponse.getTid())
			.next_redirect_pc_url(requestResponse.getNext_redirect_pc_url())
			.createdAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
			.build();
	}
}
