package com.team33.modulecore.review.dto;

import com.team33.modulecore.review.domain.Review;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewResponseDto {

    private long reviewId;
    private long itemId;
    private long userId;
    private String displayName;
    private String content;
    private int star;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    private ReviewResponseDto(long reviewId, long itemId, long userId, String displayName,
        String content, int star, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.reviewId = reviewId;
        this.itemId = itemId;
        this.userId = userId;
        this.displayName = displayName;
        this.content = content;
        this.star = star;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewResponseDto from(Review review) {
       return ReviewResponseDto.builder()
           .reviewId(review.getReviewId())
           .itemId(review.getItem().getId())
           .userId(review.getUser().getId())
           .displayName(review.getUser().getDisplayName())
           .content(review.getContent())
           .star(review.getStar())
           .createdAt(review.getCreatedAt())
           .updatedAt(review.getUpdatedAt())
           .build();
    }
}
