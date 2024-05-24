package com.team33.moduleexternalapi.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoSubsCancelResponse {
	private String cid;
	private String sid;
	private String status;
	private LocalDateTime created_at;
	private LocalDateTime inactivated_at;
	private LocalDateTime last_approved_at;
}
