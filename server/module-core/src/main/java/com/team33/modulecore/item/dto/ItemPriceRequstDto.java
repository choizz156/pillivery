package com.team33.modulecore.item.dto;

import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemPriceRequstDto {

    @Min(0)
    private int low = 0;

    @Min(0)
    private int high = 0;
}

