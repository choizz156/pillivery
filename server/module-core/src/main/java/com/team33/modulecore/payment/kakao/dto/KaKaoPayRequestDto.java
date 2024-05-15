package com.team33.modulecore.payment.kakao.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.Getter;

@Getter
public class KaKaoPayRequestDto {
	private String tid;
	private String next_redirect_pc_url;
	private ZonedDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
}
