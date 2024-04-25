package com.team33.modulecore.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionFactPostDto {
    private String ingredient;
    private String volume;
}
