package com.team33.modulecore.core.category.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	boolean existsByCategoryName(CategoryName categoryName);

	Category findByCategoryName(CategoryName categoryName);
}
