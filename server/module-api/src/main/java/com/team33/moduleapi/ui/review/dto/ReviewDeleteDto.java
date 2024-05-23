package com.team33.moduleapi.ui.review.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ReviewDeleteDto {

	private Long reviewId;
	private Long userId;
	private Long itemId;
}
