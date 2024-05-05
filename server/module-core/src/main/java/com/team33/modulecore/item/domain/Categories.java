package com.team33.modulecore.item.domain;

import com.team33.modulecore.category.domain.CategoryName;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class Categories {

    private final Set<CategoryName> categoryNames;

    public Categories(Set<CategoryName> categoryNames) {
        this.categoryNames = new HashSet<>();
        this.categoryNames.addAll(categoryNames);
    }

    public void removeCategoryName(CategoryName categoryName) {
        this.categoryNames.remove(categoryName);
    }

    public void addCategoryName(CategoryName categoryName) {
        this.categoryNames.add(categoryName);
    }
}
