package com.team33.modulecore.category.domain.repository;


import com.team33.modulecore.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {


}
