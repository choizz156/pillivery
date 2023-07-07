package com.team33.modulecore.domain.item.dto;

import com.team33.modulecore.domain.item.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSimpleResponseDto {

    private long itemId;
    private Brand brand;
    private String thumbnail;
    private String title;
    private int capacity;
    private int price;
    private int discountRate;
    private int disCountPrice;
}
