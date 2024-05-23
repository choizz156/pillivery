package com.team33.modulecore.review.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewContext {

	private Long reviewId;
	private Long orderId;
	private String content;
	private double star;
	private Long itemId;
	private Long userId;
	private String displayName;

	@Builder
	public ReviewContext(Long reviewId, Long orderId, String content, double star, Long itemId, Long userId, String displayName) {
		this.reviewId = reviewId;
		this.orderId = orderId;
		this.content = content;
		this.star = star;
		this.itemId = itemId;
		this.userId = userId;
		this.displayName = displayName;
	}
}
