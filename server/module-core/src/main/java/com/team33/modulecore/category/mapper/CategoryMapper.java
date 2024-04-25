//package com.team33.modulecore.category.mapper;
//
//import com.team33.modulecore.category.dto.CategoryPostDto;
//import com.team33.modulecore.category.dto.CategoryDto.Response;
//import com.team33.modulecore.category.domain.Category;
//import org.mapstruct.Mapper;
//
//@Mapper(componentModel = "spring")
//public interface CategoryMapper {
//
//    default Category categoryPostDtoToCategory(CategoryPostDto categoryPostDto) {
//        Category category = new Category();
//        category.setCategoryName(categoryPostDto.getCategoryName());
//        return category;
//    }
//
//
//    default Response categoryToCategoryResponseDto(Category category) {
//        Response categoryResponseDto = new Response();
//        categoryResponseDto.setCategoryName(category.getCategoryName());
//        return categoryResponseDto;
//    }
//
//}
