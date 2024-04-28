package com.team33.modulecore.category.domain.mock;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.category.domain.repository.CategoryQueryRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;

public class MemoryCategoryRepository implements CategoryQueryRepository {

    private EntityManager em;

    public MemoryCategoryRepository(EntityManager em) {
        this.em = em;
        Arrays.stream(CategoryName.values())
            .map(Category::of)
            .forEach(em::persist);
    }

    @Override
    public Optional<Category> findByCategoryName(CategoryName categoryName) {
        Category category = em.createQuery(
                "SELECT c FROM Category c where c.categoryName = :categoryName",
                Category.class
            )
            .setParameter("categoryName", categoryName)
            .getSingleResult();

        return Optional.ofNullable(category);
    }

    @Override
    public List<Category> findCategoriesByCategoryName(List<CategoryName> categoryNames) {
        return em.createQuery(
                "SELECT c FROM Category c WHERE c.categoryName IN :categoryNames",
                Category.class
            )
            .setParameter("categoryNames", categoryNames)
            .getResultList();
    }
}
