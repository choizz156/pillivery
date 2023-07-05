package team33.modulecore.domain.category.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team33.modulecore.domain.category.entity.Category;
import team33.modulecore.domain.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }


    public void verifyExistCategory(String categoryName) {
        Optional<Category> byCategoryName =
                categoryRepository.findByCategoryName(categoryName);

        if (byCategoryName.isEmpty()) { // 카테고리 이름이 존재하지 않는다면
            throw new RuntimeException();
        }
    }

    public Long findCategoryId(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName).get().getCategoryId();
    }



}
