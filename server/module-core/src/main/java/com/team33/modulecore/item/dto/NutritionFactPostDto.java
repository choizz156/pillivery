package com.team33.modulecore.item.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionFactPostDto {

    @NotBlank
    private String ingredient;

    @NotBlank
    private String volume;
}
