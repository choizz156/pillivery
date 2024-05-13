package com.team33.modulecore.review.dto.query;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;
import com.team33.modulecore.review.domain.ReviewStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewQueryDto {

    private long reviewId;
    private long itemId;
    private long userId;
    private String displayName;
    private String content;
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
        String displayName,
        String content,
        double star,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
    ) {
        this.reviewId = reviewId;
        this.itemId = itemId;
        this.userId = userId;
        this.displayName = displayName;
        this.content = content;
        this.star = star;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
