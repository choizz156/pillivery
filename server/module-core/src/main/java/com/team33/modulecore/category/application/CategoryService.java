package com.team33.modulecore.category.application;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.category.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getCategories(List<CategoryName> category) {

        return category.stream()
            .map(Category::new)
            .map(categoryRepository::save)
            .collect(Collectors.toUnmodifiableList());
    }

    public void verifyExistCategory(String categoryName) {
        Optional<Category> byCategoryName =
            categoryRepository.findByCategoryName(categoryName);

        if (byCategoryName.isEmpty()) { // 카테고리 이름이 존재하지 않는다면
            throw new RuntimeException();
        }
    }

    public Long findCategoryId(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName).get().getId();
    }
}

