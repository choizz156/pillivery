package team33.modulecore.domain.wish.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import team33.modulecore.domain.item.mapper.ItemMapper;
import team33.modulecore.domain.wish.dto.WishDto.WishItemResponseDto;
import team33.modulecore.domain.wish.dto.WishDto.WishResponseDto;
import team33.modulecore.domain.wish.entity.Wish;


@Mapper(componentModel = "spring")
public interface WishMapper {

    default WishResponseDto wishToWishDto(Wish wish) {
        WishResponseDto wishResponseDto = new WishResponseDto();

        wishResponseDto.setItemId(wish.getItem().getItemId());
        wishResponseDto.setWish(wish.getIsWish());
        wishResponseDto.setTotalWishes(wish.getItem().getTotalWishes());

        return wishResponseDto;
    }


    default WishItemResponseDto wishToWishItemDto(Wish wish, ItemMapper itemMapper) {
        WishItemResponseDto wishItemResponseDto = new WishItemResponseDto();

        wishItemResponseDto.setItemId(wish.getItem().getItemId());
        wishItemResponseDto.setThumbnail(wish.getItem().getThumbnail());
        wishItemResponseDto.setTitle(wish.getItem().getTitle());
        wishItemResponseDto.setContent(wish.getItem().getContent());
        wishItemResponseDto.setCapacity(wish.getItem().getCapacity());
        wishItemResponseDto.setPrice(wish.getItem().getPrice());
        wishItemResponseDto.setDiscountRate(wish.getItem().getDiscountRate());
        wishItemResponseDto.setDiscountPrice(wish.getItem().getDiscountPrice());
        wishItemResponseDto.setBrand(wish.getItem().getBrand());
        wishItemResponseDto.setNutritionFacts(
                itemMapper.nutritionFactToNutritionFactResponseDto(wish.getItem().getNutritionFacts()));
        wishItemResponseDto.setStarAvg(wish.getItem().getStarAvg());
        wishItemResponseDto.setReviewSize(wish.getItem().getReviews().size());

        return wishItemResponseDto;
    }

    default List<WishItemResponseDto> wishesToWishItemResponse(List<Wish> wishes, ItemMapper itemMapper) {
        if (wishes == null) return null;

        List<WishItemResponseDto> wishItemResponses = new ArrayList<>();

        for (Wish wish : wishes) {
            System.out.println("============================");
            System.out.println(wish.getItem().getItemId());
            System.out.println("===========================");
            wishItemResponses.add(wishToWishItemDto(wish, itemMapper));
        }

        return wishItemResponses;
    }
}
