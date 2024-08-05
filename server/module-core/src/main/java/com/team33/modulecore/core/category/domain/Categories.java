package com.team33.modulecore.core.category.domain;

import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class Categories {

    private Set<CategoryName> categoryNameSet;

    public Categories(Set<CategoryName> categoryNameSet) {
        this.categoryNameSet = new HashSet<>();
        this.categoryNameSet.addAll(categoryNameSet);
    }

    public void removeCategoryName(CategoryName categoryName) {
        this.categoryNameSet.remove(categoryName);
    }

    public void addCategoryName(CategoryName categoryName) {
        this.categoryNameSet.add(categoryName);
    }
}
