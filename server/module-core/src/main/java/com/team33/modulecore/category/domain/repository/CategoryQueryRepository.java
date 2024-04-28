package com.team33.modulecore.category.domain.repository;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface CategoryQueryRepository extends Repository<Category, Long> {

    Optional<Category> findByCategoryName(CategoryName categoryName);

    List<Category> findCategoriesByCategoryName(List<CategoryName> categoryNames);
}
