package com.team33.modulecore.review.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.application.ItemCommandService;
import com.team33.modulecore.review.domain.ReviewContext;
import com.team33.modulecore.review.domain.ReviewStatus;
import com.team33.modulecore.review.domain.entity.Review;
import com.team33.modulecore.review.domain.repository.ReviewCommandRepository;
import com.team33.modulecore.user.application.UserService;

class ReviewCommandServiceTest {

	@DisplayName("리뷰 생성를 저장할 수 있다.")
	@Test
	void 리뷰_저장() throws Exception {
		//given
		ReviewContext context = ReviewContext.builder()
			.content("Test review")
			.star(4.5)
			.itemId(1L)
			.userId(1L)
			.build();

		Review reviewSample = FixtureMonkeyFactory.get()
			.giveMeBuilder(Review.class)
			.set("id", 1L)
			.set("content", "Test review")
			.set("userId", 1L)
			.set("itemId", 1L)
			.set("star", 4.5)
			.set("reviewStatus", ReviewStatus.ACTIVE)
			.sample();

		ReviewCommandRepository reviewCommandRepository = mock(ReviewCommandRepository.class);
		UserService userService = mock(UserService.class);
		ItemCommandService itemCommandService = mock(ItemCommandService.class);

		when(reviewCommandRepository.save(any(Review.class))).thenReturn(reviewSample);

		ReviewCommandService reviewCommandService = new ReviewCommandService(reviewCommandRepository, userService,
			itemCommandService);

		//when
		reviewCommandService.createReview(context);

		//then
		verify(reviewCommandRepository, times(1)).save(any(Review.class));
		verify(userService, times(1)).addReviewId(anyLong(), anyLong());
		verify(itemCommandService, times(1)).addReviewId(anyLong(), anyLong(), anyDouble());
	}

	@DisplayName("리뷰를 수정할 수 있다.")
	@Test
	void 리뷰_수정() throws Exception {
		//given
		Review review = FixtureMonkeyFactory.get().giveMeBuilder(Review.class)
			.set("id", 1L)
			.set("content", "Test review")
			.set("userId", 1L)
			.set("itemId", 1L)
			.set("star", 4.5)
			.sample();

		ReviewContext context = ReviewContext.builder()
			.userId(1L)
			.reviewId(1L)
			.itemId(1L)
			.content("new content")
			.star(3.5)
			.build();

		ReviewCommandRepository reviewCommandRepository = mock(ReviewCommandRepository.class);
		when(reviewCommandRepository.findById(anyLong())).thenReturn(Optional.of(review));

		ReviewCommandService reviewCommandService = new ReviewCommandService(reviewCommandRepository, null, null);

		//when
		Review update = reviewCommandService.updateReview(context);

		//then
		assertThat(update.getContent()).isEqualTo("new content");
		assertThat(update.getStar()).isEqualTo(3.5);

		verify(reviewCommandRepository, times(1)).findById(anyLong());
	}

	@DisplayName("리뷰를 삭제할 수 있다.")
	@Test
	void 리뷰_삭제() throws Exception {
		//given
		Review review = FixtureMonkeyFactory.get().giveMeBuilder(Review.class)
			.set("id", 1L)
			.set("content", "Test review")
			.set("userId", 1L)
			.set("itemId", 1L)
			.set("star", 4.5)
			.sample();

		ReviewContext context = ReviewContext.builder()
			.reviewId(1L)
			.userId(1L)
			.itemId(1L)
			.build();

		ReviewCommandRepository reviewCommandRepository = mock(ReviewCommandRepository.class);
		when(reviewCommandRepository.findById(anyLong())).thenReturn(Optional.of(review));
		UserService userService = mock(UserService.class);
		ItemCommandService itemCommandService = mock(ItemCommandService.class);

		ReviewCommandService reviewCommandService = new ReviewCommandService(
			reviewCommandRepository,
			userService,
			itemCommandService
		);

		//when
		reviewCommandService.deleteReview(context);

		//then
		assertThat(review.getReviewStatus()).isEqualByComparingTo(ReviewStatus.INACTIVE);

		verify(userService, times(1)).deleteReviewId(anyLong(), anyLong());
		verify(itemCommandService, times(1)).deleteReviewId(anyLong(), any(Review.class));
	}

}