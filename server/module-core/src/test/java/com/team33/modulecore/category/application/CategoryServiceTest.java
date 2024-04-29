package com.team33.modulecore.category.application;

import static com.team33.modulecore.category.domain.CategoryName.EYE;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.repository.CategoryQueryRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CategoryServiceTest {


    @DisplayName("카테고리를 생성할 수 있다.")
    @Test
    void
    카테고리_생성() throws Exception {

        //given
        var categoryQueryRepository = mock(CategoryQueryRepository.class);
        var categories = List.of(Category.of(EYE));
        given(categoryQueryRepository.findCategoriesByCategoryName(anyList()))
            .willReturn(categories);

        var categoryService = new CategoryService(null, categoryQueryRepository);

        //when
        List<Category> results = categoryService.getCategories(List.of(EYE));
        //then
        assertThat(results).hasSize(1)
            .extracting("categoryName")
            .containsExactlyInAnyOrder(EYE);
    }
}
