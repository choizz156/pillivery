package com.team33.modulecore.core.review.dto.query;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;
import com.team33.modulecore.core.review.domain.ReviewStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewQueryDto {

    private long reviewId;
    private long itemId;
    private long userId;
    private String content;
    private String displayName;
    private double star;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ReviewStatus reviewStatus;

    @QueryProjection
    @Builder
    public ReviewQueryDto(
        long reviewId,
        long itemId,
        long userId,
        String content,
        String displayName,
        double star,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        ReviewStatus reviewStatus
    ) {
        this.reviewId = reviewId;
        this.itemId = itemId;
        this.userId = userId;
        this.content = content;
		this.displayName = displayName;
		this.star = star;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewStatus = reviewStatus;
    }
}
