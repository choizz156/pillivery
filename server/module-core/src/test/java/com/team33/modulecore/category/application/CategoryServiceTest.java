package com.team33.modulecore.category.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.category.domain.repository.CategoryRepository;
import com.team33.modulecore.config.QueryDslConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "com.team33.modulecore")
@EntityScan("com.team33.modulecore")
@ContextConfiguration(classes = {CategoryService.class, CategoryRepository.class, QueryDslConfig.class})
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("카테고리를 생성할 수 있다.")
    @Test
    void
    카테고리_생성() throws Exception {

        //given
        var categories = List.of(CategoryName.EYE,CategoryName.BONE);

        //when
        var results = categoryService.getCategories(categories);

        //then
    	assertThat(results).hasSize(2)
            .extracting("categoryName")
            .containsExactlyInAnyOrder(CategoryName.EYE, CategoryName.BONE);
    }

}