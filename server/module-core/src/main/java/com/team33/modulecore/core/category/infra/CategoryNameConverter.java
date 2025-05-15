package com.team33.modulecore.core.category.infra;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.team33.modulecore.core.category.domain.Categories;
import com.team33.modulecore.core.category.domain.CategoryName;

@Converter
public class CategoryNameConverter implements AttributeConverter<Categories, String> {

    @Override
    public String convertToDatabaseColumn(Categories attribute) {
        if(attribute == null) {
            return null;
        }

        return attribute.getCategoryNameSet().stream()
            .map(Enum::name)
            .collect(Collectors.joining(","));
    }

    @Override
    public Categories convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }

        String[] names = dbData.split(",");
        Set<CategoryName> categorySet = Arrays.stream(names)
            .map(CategoryName::valueOf)
            .collect(Collectors.toSet());

        return new Categories(categorySet);
    }
}
