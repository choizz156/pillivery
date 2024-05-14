package com.team33.modulecore.review.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.security.sasl.AuthenticationException;

import org.springframework.security.authentication.AuthenticationServiceException;

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long id;

	private String content;

	private double star;

	@Enumerated(value = EnumType.STRING)
	private ReviewStatus reviewStatus;

	private Long itemId;

	private Long userId;

	@Builder
	private Review(String content, double star, Long userId, Long itemId, ReviewStatus reviewStatus) {
		this.content = content;
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
			.reviewStatus(ReviewStatus.ACTIVE)
			.build();
	}

	public Review update(ReviewContext context) {

		checkWriter(context);
		checkReviewToItem(context);

		this.content = context.getContent();
		this.star = context.getStar();
		return this;
	}

	public void delete(ReviewContext context) {
		checkWriter(context);
		checkReviewToItem(context);

		this.reviewStatus = ReviewStatus.INACTIVE;
	}

	private void checkReviewToItem(ReviewContext context) {
		if (!this.itemId.equals(context.getItemId())) {
			throw new IllegalArgumentException("리뷰와 상품이 일치하지 않습니다.");
		}
	}

	private void checkWriter(ReviewContext context) {
		if (!this.userId.equals(context.getUserId())) {
			throw new IllegalArgumentException("리뷰 작성자만이 수정할 수 있습니다.");
		}
	}
}
