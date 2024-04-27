package com.team33.modulecore.item.dto;

import com.team33.modulecore.item.domain.entity.NutritionFact;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NutritionFactResponseDto {

    private String ingredient;
    private String volume;

    public static NutritionFactResponseDto from(NutritionFact fact) {
        return new NutritionFactResponseDto(fact.getIngredient(), fact.getVolume());
    }
}
