package com.team33.moduleapi.controller.review;

import com.team33.modulecore.domain.item.mapper.ItemMapper;
import com.team33.modulecore.domain.item.service.ItemService;
import com.team33.modulecore.domain.order.service.ItemOrderService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.review.dto.ReviewDto;
import com.team33.modulecore.domain.review.entity.Review;
import com.team33.modulecore.domain.review.mapper.ReviewMapper;
import com.team33.modulecore.domain.review.service.ReviewService;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.user.service.UserService;
import com.team33.modulecore.global.response.MultiResponseDto;
import com.team33.modulecore.global.response.SingleResponseDto;
import groovy.util.logging.Slf4j;
import java.util.List;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final OrderService orderService;
    private final ItemOrderService itemOrderService;

    @PostMapping("/{itemOrder-id}") // 주문 상세 내역에서 작성
    public ResponseEntity postReview(@PathVariable("itemOrder-id") @Positive long itemOrderId,
                                     @RequestBody ReviewDto reviewDto) {

        Review review = reviewService.createReview(
                reviewMapper.reviewDtoToReview(itemOrderId, itemOrderService, userService, orderService, itemService, reviewDto));

        return  new ResponseEntity<>(
                new SingleResponseDto<>(reviewMapper.reviewToReviewResponseDto(review)), HttpStatus.CREATED);
    }

    @PatchMapping("/{review-id}") // 마이페이지 작성글 관리, 아이템 상세페이지
    public ResponseEntity updateReview(@PathVariable("review-id") @Positive long reviewId,
                                       @RequestBody ReviewDto reviewDto) {

        Review review = reviewMapper.reviewDtoToReview(reviewId, userService, reviewService, reviewDto);

        Review updatedReview = reviewService.updateReview(review);

        return  new ResponseEntity<>(new SingleResponseDto<>(
                reviewMapper.reviewToReviewResponseDto(updatedReview)), HttpStatus.OK);
    }

    @GetMapping("/mypage")
    public ResponseEntity getUserReviews(@Positive @RequestParam(value="page", defaultValue="1") int page,
                                         @Positive @RequestParam(value="size", defaultValue="7") int size,
                                         @RequestParam(value="sort", defaultValue="reviewId") String sort) {

        Page<Review> pageReviews = reviewService.findReviews(userService.getLoginUser(), page-1, size, sort);

        List<Review> reviews = pageReviews.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(
                reviewMapper.reviewsToReviewDetailResponseDtos(reviews, itemMapper), pageReviews), HttpStatus.OK);
    }

    @GetMapping("/{review-id}")
    public ResponseEntity getReview(@PathVariable("review-id") @Positive long reviewId) {

        Review review = reviewService.findReview(reviewId);

        return  new ResponseEntity<>(new SingleResponseDto<>(
                reviewMapper.reviewToReviewResponseDto(review)), HttpStatus.OK);
    }

    @DeleteMapping("/{review-id}") // 마이페이지 작성글 관리, 아이템 상세페이지
    public ResponseEntity deleteReview(@PathVariable("review-id") @Positive long reviewId) {

        User user = userService.getLoginUser();
        reviewService.deleteReview(reviewId, user.getUserId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
