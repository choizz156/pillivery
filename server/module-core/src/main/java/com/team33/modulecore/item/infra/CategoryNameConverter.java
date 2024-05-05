package com.team33.modulecore.item.infra;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Categories;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;

public class CategoryNameConverter implements AttributeConverter<Categories, String> {

    @Override
    public String convertToDatabaseColumn(Categories attribute) {
        if(attribute == null) {
            return null;
        }

        return attribute.getCategoryNames().stream()
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
