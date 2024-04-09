//package com.team33.modulecore.domain.review.mapper;
//
//
//import com.team33.modulecore.domain.review.entity.Review;
//import java.util.ArrayList;
//import java.util.List;
//import org.mapstruct.Mapper;
//import com.team33.modulecore.domain.item.mapper.ItemMapper;
//import com.team33.modulecore.domain.item.service.ItemService;
//import com.team33.modulecore.domain.order.entity.OrderItem;
//import com.team33.modulecore.domain.order.service.OrderItemService;
//import com.team33.modulecore.domain.order.service.OrderService;
//import com.team33.modulecore.domain.review.dto.ReviewDetailResponseDto;
//import com.team33.modulecore.domain.review.dto.ReviewDto;
//import com.team33.modulecore.domain.review.dto.ReviewResponseDto;
//import com.team33.modulecore.domain.review.service.ReviewService;
//import com.team33.modulecore.domain.user.entity.User;
//import com.team33.modulecore.domain.user.service.UserService;
//import com.team33.modulecore.global.exception.BusinessLogicException;
//import com.team33.modulecore.global.exception.ExceptionCode;
//
//@Mapper(componentModel = "spring")
//public interface ReviewMapper {
//
//    default Review reviewDtoToReview(long itemOrderId, OrderItemService orderItemService,
//                                     UserService userService, OrderService orderService,
//                                     ItemService itemService, ReviewDto reviewDto) { // 등록
//
//        Review review = new Review();
//        review.setUser(userService.getLoginUser());
//
//        OrderItem orderItem = orderItemService.findItemOrder(itemOrderId);
//
//        if (!orderService.isShopper(orderItem.getItem().getItemId(), review.getUser().getUserId())) {
//            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED_USER);
//        } // 아이템 구매자만 리뷰를 작성할 수 있음
//
//        review.setQuantity(orderItem.getQuantity());
//        review.setItem(itemService.findVerifiedItem(orderItem.getItem().getItemId()));
//        review.setContent(reviewDto.getContent());
//        review.setStar(reviewDto.getStar());
//
//        return review;
//    }
//
//    default Review reviewDtoToReview(long reviewId, UserService userService,
//                                     ReviewService reviewService, ReviewDto reviewDto) { // 수정
//
//        User user = userService.getLoginUser();
//
//        if(user.getUserId() != reviewService.findReviewWriter(reviewId)) {
//            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED_USER);
//        } // 리뷰 작성자만 리뷰를 수정할 수 있음
//
//        Review review = new Review();
//        review.setReviewId(reviewId);
//        review.setUser(userService.getLoginUser());
//        review.setContent(reviewDto.getContent());
//        review.setStar(reviewDto.getStar());
//
//        return review;
//    }
//
//    default ReviewResponseDto reviewToReviewResponseDto(Review review) {
//
//        ReviewResponseDto reviewResponseDto = new ReviewResponseDto();
//        reviewResponseDto.setReviewId(review.getReviewId());
//        reviewResponseDto.setItemId(review.getItem().getItemId());
//        reviewResponseDto.setUserId(review.getUser().getUserId());
//        reviewResponseDto.setDisplayName(review.getUser().getDisplayName());
//        reviewResponseDto.setContent(review.getContent());
//        reviewResponseDto.setStar(review.getStar());
//        reviewResponseDto.setCreatedAt(review.getCreatedAt());
//        reviewResponseDto.setUpdatedAt(review.getUpdatedAt());
//
//        return reviewResponseDto;
//    }
//
//    default List<ReviewResponseDto> reviewsToReviewResponseDtos(List<Review> reviews) {
//
//        if(reviews == null) return null;
//
//        List<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();
//
//        for(Review review : reviews) {
//            reviewResponseDtos.add(reviewToReviewResponseDto(review));
//        }
//
//        return reviewResponseDtos;
//    }
//
//    default ReviewDetailResponseDto reviewToReviewDetailResponseDto(Review review, ItemMapper itemMapper) {
//
//        ReviewDetailResponseDto reviewResponseDto = new ReviewDetailResponseDto();
//        reviewResponseDto.setReviewId(review.getReviewId());
//        reviewResponseDto.setItem(itemMapper.itemToItemSimpleResponseDto(review.getItem()));
//        reviewResponseDto.setQuantity(review.getQuantity());
//        reviewResponseDto.setUserId(review.getUser().getUserId());
//        reviewResponseDto.setContent(review.getContent());
//        reviewResponseDto.setStar(review.getStar());
//        reviewResponseDto.setCreatedAt(review.getCreatedAt());
//        reviewResponseDto.setUpdatedAt(review.getUpdatedAt());
//
//        return reviewResponseDto;
//    }
//
//    default List<ReviewDetailResponseDto> reviewsToReviewDetailResponseDtos(List<Review> reviews, ItemMapper itemMapper) {
//
//        if(reviews == null) return null;
//
//        List<ReviewDetailResponseDto> reviewResponseDtos = new ArrayList<>();
//
//        for(Review review : reviews) {
//            reviewResponseDtos.add(reviewToReviewDetailResponseDto(review, itemMapper));
//        }
//
//        return reviewResponseDtos;
//    }
//}
