package com.team33.moduleapi.ui.payment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.team33.modulecore.payment.kakao.dto.KakaoRequestResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KaKaoPayNextUrl {
	private String tid;
	private String next_redirect_pc_url;
	private ZonedDateTime createAt;

	@Builder
	private KaKaoPayNextUrl(String tid, String next_redirect_pc_url, ZonedDateTime createAt) {
		this.tid = tid;
		this.next_redirect_pc_url = next_redirect_pc_url;
		this.createAt = createAt;
	}

	public static KaKaoPayNextUrl from(KakaoRequestResponse requestResponse) {
		return KaKaoPayNextUrl.builder()
			.tid(requestResponse.getTid())
			.next_redirect_pc_url(requestResponse.getNext_redirect_pc_url())
			.createAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
			.build();
	}
}
