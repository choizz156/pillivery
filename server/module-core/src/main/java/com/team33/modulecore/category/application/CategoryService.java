package com.team33.modulecore.category.application;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.category.domain.repository.CategoryQueryRepository;
import com.team33.modulecore.category.domain.repository.CategoryRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    public Category save(CategoryName name) {
        return categoryRepository.save(Category.of(name));
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories(List<CategoryName> category) {
        List<Category> categories = categoryQueryRepository.findCategoriesByCategoryName(category);

        return Collections.unmodifiableList(categories);
    }

//    public void verifyExistCategory(String categoryName) {
//        Optional<Category> byCategoryName =
//            categoryRepository.findByCategoryName(categoryName);
//
//        if (byCategoryName.isEmpty()) { // 카테고리 이름이 존재하지 않는다면
//            throw new RuntimeException();
//        }
//    }
//
//    public Long findCategoryId(String categoryName) {
//        return categoryRepository.findByCategoryName(categoryName).get().getId();
//    }
}

