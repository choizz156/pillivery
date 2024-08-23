package com.team33.moduleapi.api.review.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewPostDto {
	private long userId;
	private long itemId;
	private long orderId;
	private String displayName;
	private String content;
	private double star;
}
