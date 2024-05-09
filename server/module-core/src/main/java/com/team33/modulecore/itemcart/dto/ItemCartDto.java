package com.team33.modulecore.itemcart.dto;

import java.time.ZonedDateTime;

import javax.validation.constraints.Positive;

import com.team33.modulecore.item.dto.query.ItemSimpleResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ItemCartDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {

        @Positive
        private Long itemCartId;

        @Positive
        private Integer quantity;

        private Integer period;
        private boolean buyNow;
        private boolean subscription;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long itemCartId;
        private Integer quantity;
        private Integer period;
        private boolean buyNow;
        private boolean subscription;
        private ItemSimpleResponseDto item;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
    }
}
