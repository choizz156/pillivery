package com.team33.moduleapi.ui.review.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewPostDto {
	private Long userId;
	private Long itemId;
	private Long orderId;
	private String displayName;
	private String content;
	private double star;
}
