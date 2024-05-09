package com.team33.modulecore.category.domain.repository;

import com.team33.modulecore.category.domain.entity.Category;
import com.team33.modulecore.category.domain.CategoryName;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {

    private final EntityManager entityManager;

    @Override
    public Category findByCategoryName(CategoryName categoryName) {
        Category category = entityManager
            .createQuery(
                "SELECT c FROM Category c where c.categoryName = :categoryName",
                Category.class
            )
            .setParameter("categoryName", categoryName)
            .getSingleResult();

        if(category == null) {
            throw new IllegalStateException("카테고리 이름을 찾을 수 없습니다. " + categoryName);
        }

        return category;
    }

    @Override
    public List<Category> findCategoriesByCategoryName(List<CategoryName> categoryNames) {
        return entityManager
            .createQuery(
                "SELECT c FROM Category c WHERE c.categoryName IN :categoryNames",
                Category.class
            )
            .setParameter("categoryNames", categoryNames)
            .getResultList();
    }
}
