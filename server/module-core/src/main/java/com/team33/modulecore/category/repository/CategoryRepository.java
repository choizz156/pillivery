package com.team33.modulecore.category.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team33.modulecore.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryName(String categoryName);

}
