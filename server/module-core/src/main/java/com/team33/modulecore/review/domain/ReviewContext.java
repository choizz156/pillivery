package com.team33.modulecore.review.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewContext {

	private String content;
	private double star;
	private Long itemId;
	private Long userId;

	@Builder
	public ReviewContext(String content, double star, Long itemId, Long userId) {
		this.content = content;
		this.star = star;
		this.itemId = itemId;
		this.userId = userId;
	}
}
