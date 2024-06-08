package com.team33.modulecore.review.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewContext {

	private long reviewId;
	private long orderId;
	private String content;
	private double star;
	private long itemId;
	private long userId;
	private String displayName;

	@Builder
	public ReviewContext(long reviewId, long orderId, String content, double star, long itemId, long userId, String displayName) {
		this.reviewId = reviewId;
		this.orderId = orderId;
		this.content = content;
		this.star = star;
		this.itemId = itemId;
		this.userId = userId;
		this.displayName = displayName;
	}
}
