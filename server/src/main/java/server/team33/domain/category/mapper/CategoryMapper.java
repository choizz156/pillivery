package server.team33.domain.category.mapper;

import org.mapstruct.Mapper;
import server.team33.domain.category.entity.Category;
import server.team33.domain.category.dto.CategoryDto.Post;
import server.team33.domain.category.dto.CategoryDto.Response;

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
