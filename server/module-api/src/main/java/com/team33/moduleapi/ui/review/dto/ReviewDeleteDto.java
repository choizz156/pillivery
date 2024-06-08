package com.team33.moduleapi.ui.review.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ReviewDeleteDto {
	private long reviewId;
	private long itemId;
	private long userId;
}
