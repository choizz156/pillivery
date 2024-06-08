package com.team33.moduleapi.ui.review.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewPatchDto {
	private long userId;
	private long itemId;
	private long reviewId;
	private String content;
	private double star;
}
