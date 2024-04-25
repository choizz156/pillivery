package com.team33.modulecore.category.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class CategoryDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private String categoryName;
    }
}
