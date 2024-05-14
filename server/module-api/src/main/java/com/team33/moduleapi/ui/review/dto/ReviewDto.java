package com.team33.moduleapi.ui.review.dto;

import lombok.Data;

@Data
public class ReviewDto {
	private Long reviewId;
	private Long userId;
	private Long itemId;
	private Long orderId;
	;;
	private String displayName;
	private String content;
	private double star;
}
