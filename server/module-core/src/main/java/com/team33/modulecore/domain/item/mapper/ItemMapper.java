package com.team33.modulecore.domain.item.mapper;


import com.team33.modulecore.domain.category.dto.CategoryDto;
import com.team33.modulecore.domain.category.entity.Category;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.nutritionFact.dto.NutritionFactDto.Post;
import com.team33.modulecore.domain.nutritionFact.dto.NutritionFactDto.Response;
import com.team33.modulecore.domain.nutritionFact.entity.NutritionFact;
import com.team33.modulecore.domain.review.entity.Review;
import com.team33.modulecore.domain.review.mapper.ReviewMapper;
import com.team33.modulecore.domain.review.service.ReviewService;
import com.team33.modulecore.domain.talk.entity.Talk;
import com.team33.modulecore.domain.talk.mapper.TalkMapper;
import com.team33.modulecore.domain.talk.service.TalkService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import com.team33.modulecore.domain.item.dto.ItemDto.ItemCategoryResponse;
import com.team33.modulecore.domain.item.dto.ItemDto.ItemDetailResponse;
import com.team33.modulecore.domain.item.dto.ItemDto.ItemMainTop9Response;
import com.team33.modulecore.domain.item.dto.ItemDto.post;
import com.team33.modulecore.domain.item.dto.ItemSimpleResponseDto;
import com.team33.modulecore.domain.item.service.ItemService;
import com.team33.modulecore.global.response.MultiResponseDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    default ItemDetailResponse itemToItemDetailResponseDto(Item item) {
        ItemDetailResponse itemDetailResponseDto = new ItemDetailResponse();

        itemDetailResponseDto.setItemId(item.getItemId());
        itemDetailResponseDto.setThumbnail(item.getThumbnail());
        itemDetailResponseDto.setDescriptionImage(item.getDescriptionImage());
        itemDetailResponseDto.setTitle(item.getTitle());
        itemDetailResponseDto.setContent(item.getContent());
        itemDetailResponseDto.setExpiration(item.getExpiration());
        itemDetailResponseDto.setBrand(item.getBrand());
        itemDetailResponseDto.setSales(item.getSales());
        itemDetailResponseDto.setPrice(item.getPrice());
        itemDetailResponseDto.setCapacity(item.getCapacity());
        itemDetailResponseDto.setServingSize(item.getServingSize());
        itemDetailResponseDto.setDiscountRate(item.getDiscountRate());
        itemDetailResponseDto.setDiscountPrice(item.getDiscountPrice());
        itemDetailResponseDto.setNutritionFacts(nutritionFactToNutritionFactResponseDto(item.getNutritionFacts()));
        itemDetailResponseDto.setCategories(categoryToStringList(item.getCategories()));
        itemDetailResponseDto.setStarAvg(item.getStarAvg());
//        itemDetailResponseDto.setReviews(item.getReviews());
//        itemDetailResponseDto.setTalks(item.getTalks());

        return itemDetailResponseDto;
    }

    default ItemDetailResponse itemToItemDetailResponseDto(Item item, ReviewService reviewService, ReviewMapper reviewMapper,
                                                                   TalkService talkService, TalkMapper talkMapper,
                                                                   int reviewPage, int reviewSize, int talkPage, int talkSize) { // 아이템 상세 조회

        ItemDetailResponse itemDetailResponseDto = new ItemDetailResponse();

        itemDetailResponseDto.setItemId(item.getItemId());
        itemDetailResponseDto.setThumbnail(item.getThumbnail());
        itemDetailResponseDto.setDescriptionImage(item.getDescriptionImage());
        itemDetailResponseDto.setTitle(item.getTitle());
        itemDetailResponseDto.setContent(item.getContent());
        itemDetailResponseDto.setExpiration(item.getExpiration());
        itemDetailResponseDto.setBrand(item.getBrand());
        itemDetailResponseDto.setSales(item.getSales());
        itemDetailResponseDto.setPrice(item.getPrice());
        itemDetailResponseDto.setCapacity(item.getCapacity());
        itemDetailResponseDto.setServingSize(item.getServingSize());
        itemDetailResponseDto.setDiscountRate(item.getDiscountRate());
        itemDetailResponseDto.setDiscountPrice(item.getDiscountPrice());
        itemDetailResponseDto.setNutritionFacts(nutritionFactToNutritionFactResponseDto(item.getNutritionFacts()));
        itemDetailResponseDto.setCategories(categoryToStringList(item.getCategories()));
        itemDetailResponseDto.setStarAvg(item.getStarAvg());

        Page<Review> pageReview = reviewService.findItemReviews(item, reviewPage, reviewSize);
        List<Review> reviews = pageReview.getContent();
        itemDetailResponseDto.setReviews(new MultiResponseDto<>(reviewMapper.reviewsToReviewResponseDtos(reviews), pageReview));

        Page<Talk> pageTalk = talkService.findItemTalks(item, talkPage, talkSize);
        List<Talk> talks = pageTalk.getContent();
        itemDetailResponseDto.setTalks(new MultiResponseDto<>(talkMapper.talksToTalkAndCommentDtos(talks), pageTalk));

        return itemDetailResponseDto;
    }

    default Item itemPostDtoToItem(post post) {
        System.out.println("==================="+post);
        Item item = new Item();

        item.setThumbnail(post.getThumbnail());
        item.setDescriptionImage(post.getDescriptionImage());
        item.setDiscountRate(post.getDiscountRate());
        item.setCapacity(post.getCapacity());
        item.setPrice(post.getPrice());
        item.setView(0);
        item.setSales(post.getSales());
        item.setTitle(post.getTitle());
        item.setBrand(post.getBrand());
        item.setContent(post.getContent());
        item.setExpiration(post.getExpiration());
        item.setDiscountPrice(post.getDiscountPrice());
        item.setCategories(categoryPostDtoToCategory(post.getCategories(), item));
        item.setServingSize(post.getServingSize());
        item.setNutritionFacts(nutritionFactPostDtoToNutritionFact(post.getNutritionFacts(), item));
        item.setStarAvg(post.getStarAvg());
//        item.setReviews(post.getReviews());
//        item.setTalks(post.getTalks());

        return item;
    }

    default List<Response> nutritionFactToNutritionFactResponseDto(List<NutritionFact> nutritionFacts) {
        return nutritionFacts.stream().map(nutritionFact -> {
            Response response = new Response();

            response.setIngredient(nutritionFact.getIngredient());
            response.setVolume(nutritionFact.getVolume());
            return response;
        }).collect(Collectors.toList());
    }

    default List<NutritionFact> nutritionFactPostDtoToNutritionFact(List<Post> posts, Item item) {
        return posts.stream().map(post -> {
            NutritionFact nutritionFact = new NutritionFact();

            nutritionFact.setIngredient(post.getIngredient());
            nutritionFact.setVolume(post.getVolume());
            nutritionFact.setItem(item);
            return nutritionFact;
        }).collect(Collectors.toList());
    }

    default List<String> categoryToStringList(List<Category> categories) {
        return categories.stream().map(Category::getCategoryName).collect(Collectors.toList());
    }


//    default List<CategoryDto.Response> categoryToCategoryResponseDto(List<Category> categories) {
//        return categories.stream().map(category -> {
//            CategoryDto.Response response = new CategoryDto.Response();
//
//            response.setCategoryName(category.getCategoryName());
//            return response;
//        }).collect(Collectors.toList());
//    }


    default List<Category> categoryPostDtoToCategory(List<CategoryDto.Post> posts, Item item) {
        return posts.stream().map(post -> {
            Category category = new Category();

            category.setCategoryName(post.getCategoryName());
            category.setItem(item);
            return category;
        }).collect(Collectors.toList());
    }



    default ItemCategoryResponse itemToItemCategoryResponseDto(Item item) {
        ItemCategoryResponse itemCategoryResponse = new ItemCategoryResponse();

        itemCategoryResponse.setItemId(item.getItemId());
        itemCategoryResponse.setThumbnail(item.getThumbnail());
        itemCategoryResponse.setTitle(item.getTitle());
        itemCategoryResponse.setContent(item.getContent());
        itemCategoryResponse.setCapacity(item.getCapacity());
        itemCategoryResponse.setPrice(item.getPrice());
        itemCategoryResponse.setDiscountRate(item.getDiscountRate());
        itemCategoryResponse.setDiscountPrice(item.getDiscountPrice());
        itemCategoryResponse.setStarAvg(item.getStarAvg());
        itemCategoryResponse.setReviewSize(item.getReviews().size());
        itemCategoryResponse.setBrand(item.getBrand());
        itemCategoryResponse.setNutritionFacts(nutritionFactToNutritionFactResponseDto(item.getNutritionFacts()));

        return itemCategoryResponse;
    }

    default List<ItemCategoryResponse> itemsToItemCategoryResponseDto(List<Item> items) {
        if(items == null) return null;

        List<ItemCategoryResponse> itemCategoryResponses = new ArrayList<>();

        for(Item item : items) {
            System.out.println(item.getItemId());
            itemCategoryResponses.add(itemToItemCategoryResponseDto(item));
        }

        return itemCategoryResponses;
    }


    default ItemSimpleResponseDto itemToItemSimpleResponseDto(Item item) {
        ItemSimpleResponseDto itemSimpleResponseDto = new ItemSimpleResponseDto();
        itemSimpleResponseDto.setItemId(item.getItemId());
        itemSimpleResponseDto.setBrand(item.getBrand());
        itemSimpleResponseDto.setThumbnail(item.getThumbnail());
        itemSimpleResponseDto.setTitle(item.getTitle());
        itemSimpleResponseDto.setCapacity(item.getCapacity());
        itemSimpleResponseDto.setPrice(item.getPrice());
        itemSimpleResponseDto.setDiscountRate(item.getDiscountRate());
        itemSimpleResponseDto.setDisCountPrice(item.getDiscountPrice());

        return itemSimpleResponseDto;
    }


    default ItemMainTop9Response itemToItemMainTop9ResponseDto(ItemService itemService) {
        ItemMainTop9Response itemMainTop9Response = new ItemMainTop9Response();

        itemMainTop9Response.setBestItem(new MultiResponseDto<>(itemsToItemCategoryResponseDto(itemService.findTop9BestItems())));
        itemMainTop9Response.setSaleItem(new MultiResponseDto<>(itemsToItemCategoryResponseDto(itemService.findTop9SaleItems())));
        itemMainTop9Response.setMdPickItem(new MultiResponseDto<>(itemsToItemCategoryResponseDto(itemService.findTop9MdPickItems())));
        return itemMainTop9Response;
    }
}
