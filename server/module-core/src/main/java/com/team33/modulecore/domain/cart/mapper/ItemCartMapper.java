//package com.team33.modulecore.domain.cart.mapper;
//
//
//import com.team33.modulecore.domain.cart.entity.ItemCart;
//import com.team33.modulecore.domain.item.mapper.ItemMapper;
//import com.team33.modulecore.domain.item.service.ItemService;
//import com.team33.modulecore.domain.user.entity.User;
//import com.team33.modulecore.domain.user.service.UserService;
//import java.util.ArrayList;
//import java.util.List;
//import org.mapstruct.Mapper;
//import com.team33.modulecore.domain.cart.dto.ItemCartDto;
//import com.team33.modulecore.domain.cart.dto.ItemCartDto.Response;
//
//@Mapper(componentModel = "spring")
//public interface ItemCartMapper {
//
//    default ItemCart itemCartPostDtoToItemCart(long itemId, UserService userService,
//                                               ItemService itemService,
//                                               ItemCartDto.Post itemCartPostDto) {
//        User user = userService.getLoginUser();
//        return ItemCart.builder()
//                .quantity(itemCartPostDto.getQuantity())
//                .period(itemCartPostDto.getPeriod() == null ? 0 : itemCartPostDto.getPeriod())
//                .buyNow(true)
//                .subscription(itemCartPostDto.isSubscription())
//                .cart(user.getCart())
//                .item(itemService.findVerifiedItem(itemId))
//                .build();
//    }
//
//    default ItemCartDto.Response itemCartToItemCartResponseDto(ItemMapper itemMapper, ItemCart itemCart) {
//        return ItemCartDto.Response.builder()
//                .itemCartId(itemCart.getItemCartId())
//                .quantity(itemCart.getQuantity())
//                .period(itemCart.getPeriod())
//                .buyNow(itemCart.isBuyNow())
//                .subscription(itemCart.isSubscription())
//                .item(itemMapper.itemToItemSimpleResponseDto(itemCart.getItem()))
//                .createdAt(itemCart.getCreatedAt())
//                .updatedAt(itemCart.getUpdatedAt())
//                .build();
//    }
//
//    default List<Response> itemCartsToItemCartResponseDtos(ItemMapper itemMapper, List<ItemCart> itemCarts) {
//        if(itemCarts == null) return null;
//
//        List<ItemCartDto.Response> itemCartResponseDtos = new ArrayList<>(itemCarts.size());
//
//        for(ItemCart itemCart : itemCarts) {
//            itemCartResponseDtos.add(itemCartToItemCartResponseDto(itemMapper, itemCart));
//        }
//
//        return itemCartResponseDtos;
//    }
//}
