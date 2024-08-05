package com.team33.moduleapi.ui.review.dto;

import java.time.ZonedDateTime;

import com.team33.modulecore.core.review.domain.entity.Review;

import lombok.Builder;
import lombok.Data;


@Data
public class ReviewDetailResponseDto { // 마이페이지 - 작성한 리뷰 조회

    private long reviewId;
    private String displayName;
    private long userId;
    private String content;
    private double star;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    public ReviewDetailResponseDto(
        long reviewId,
        String displayName,
        long userId,
        String content,
        double star,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
    ) {
        this.reviewId = reviewId;
        this.displayName = displayName;
        this.userId = userId;
        this.content = content;
        this.star = star;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewDetailResponseDto of(Review review) {
        return ReviewDetailResponseDto.builder()
            .reviewId(review.getId())
            .displayName(review.getDisplayName())
            .userId(review.getUserId())
            .content(review.getContent())
            .star(review.getStar())
            .createdAt(review.getCreatedAt())
            .updatedAt(review.getUpdatedAt())
            .build();
    }
}
