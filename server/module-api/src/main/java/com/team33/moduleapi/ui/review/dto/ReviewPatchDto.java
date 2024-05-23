package com.team33.moduleapi.ui.review.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewPatchDto {

	private Long reviewId;
	private String content;
	private double star;
}
