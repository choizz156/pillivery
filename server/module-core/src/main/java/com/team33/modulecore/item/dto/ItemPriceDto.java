package com.team33.modulecore.item.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemPriceDto {

    @NotNull
    @Min(0)
    private int low;

    @NotNull
    @Min(0)
    private int high;
}
