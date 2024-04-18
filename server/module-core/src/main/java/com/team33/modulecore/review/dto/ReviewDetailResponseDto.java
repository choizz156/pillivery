package com.team33.modulecore.review.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.team33.modulecore.item.dto.ItemSimpleResponseDto;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailResponseDto { // 마이페이지 - 작성한 리뷰 조회

    private long reviewId;
    private long userId;
    private ItemSimpleResponseDto item;
    private int quantity; // 리뷰 모달에서 아이템 구매 수량 확인
    private String content;
    private int star;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
