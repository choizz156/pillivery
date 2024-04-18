package com.team33.modulecore.category.mapper;

import com.team33.modulecore.category.dto.CategoryDto.Post;
import com.team33.modulecore.category.dto.CategoryDto.Response;
import com.team33.modulecore.category.domain.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    default Category categoryPostDtoToCategory(Post post) {
        Category category = new Category();
        category.setCategoryName(post.getCategoryName());
        return category;
    }


    default Response categoryToCategoryResponseDto(Category category) {
        Response categoryResponseDto = new Response();
        categoryResponseDto.setCategoryName(category.getCategoryName());
        return categoryResponseDto;
    }

}
