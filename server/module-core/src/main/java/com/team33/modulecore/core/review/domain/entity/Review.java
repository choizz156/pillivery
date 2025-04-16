package com.team33.modulecore.core.review.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.team33.modulecore.core.common.BaseEntity;
import com.team33.modulecore.core.review.domain.ReviewContext;
import com.team33.modulecore.core.review.domain.ReviewStatus;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review", indexes = {
		@Index(name = "idx_review_item_id", columnList = "item_id"),
		@Index(name = "idx_review_created_at", columnList = "created_at")
})
@Entity
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long id;

	private String content;

	private String displayName;

	private double star;

	@Column(name = "item_id")
	private Long itemId;

	private Long userId;

	@Enumerated(value = EnumType.STRING)
	private ReviewStatus reviewStatus;

	@Builder
	private Review(
			String content,
			String displayName,
			double star,
			Long userId,
			Long itemId,
			ReviewStatus reviewStatus) {
		this.content = content;
		this.displayName = displayName;
		this.star = star;
		this.userId = userId;
		this.itemId = itemId;
		this.reviewStatus = reviewStatus;
	}

	public static Review create(ReviewContext context) {
		return Review.builder()
				.content(context.getContent())
				.star(context.getStar())
				.itemId(context.getItemId())
				.userId(context.getUserId())
				.displayName(context.getDisplayName())
				.reviewStatus(ReviewStatus.ACTIVE)
				.build();
	}

	public Review update(ReviewContext context) {

		checkWriter(context);

		this.content = context.getContent();
		this.star = context.getStar();
		return this;
	}

	public void delete(ReviewContext context) {

		checkWriter(context);

		this.reviewStatus = ReviewStatus.INACTIVE;
	}

	private void checkWriter(ReviewContext context) {
		if (!this.userId.equals(context.getUserId())) {
			throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED_USER);
		}
	}
}
