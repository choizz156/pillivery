package team33.modulecore.domain.category.mapper;

import org.mapstruct.Mapper;
import team33.modulecore.domain.category.dto.CategoryDto.Post;
import team33.modulecore.domain.category.dto.CategoryDto.Response;
import team33.modulecore.domain.category.entity.Category;

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
