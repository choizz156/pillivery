package com.team33.modulecore.itemcart.dto;

import javax.validation.constraints.Positive;

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

}
