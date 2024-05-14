package com.team33.modulecore.review.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.common.ItemFindHelper;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.review.domain.Review;
import com.team33.modulecore.review.domain.ReviewContext;
import com.team33.modulecore.review.domain.ReviewStatus;
import com.team33.modulecore.review.mock.FakeReviewRepository;
import com.team33.modulecore.user.domain.ReviewId;
import com.team33.modulecore.user.domain.User;

class ReviewServiceTest {

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

		UserFindHelper userFindHelper = mock(UserFindHelper.class);
		ItemFindHelper itemFindHelper = mock(ItemFindHelper.class);

		User user = FixtureMonkeyFactory.get()
			.giveMeBuilder(User.class)
			.set("id", 1L)
			.set("reviewIds", new HashSet<>())
			.sample();

		Item item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information", null)
			.set("reviewIds", new HashSet<>())
			.set("statistics.starAvg", 0D)
			.set("statistics.reviewCount", 0)
			.sample();

		when(userFindHelper.findUser(anyLong())).thenReturn(user);
		when(itemFindHelper.findItem(anyLong())).thenReturn(item);

		ReviewService reviewService = new ReviewService(new FakeReviewRepository(), itemFindHelper, userFindHelper);

		//when
		Review review = reviewService.createReview(context);

		//then
		assertThat(review.getContent()).isEqualTo("Test review");
		assertThat(review.getStar()).isEqualTo(4.5D);
		assertThat(review.getUserId()).isEqualTo(1L);
		assertThat(review.getItemId()).isEqualTo(1L);
		assertThat(review.getStar()).isEqualTo(4.5);
		assertThat(review.getUserId()).isEqualTo(1L);
		assertThat(review.getItemId()).isEqualTo(1L);

		assertThat(user.getReviewIds()).hasSize(1);
		assertThat(item.getReviewIds()).hasSize(1);
		assertThat(item.getStarAvg()).isEqualTo(4.5);
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
			.itemId(1L)
			.content("new content")
			.star(3.5)
			.build();

		FakeReviewRepository reviewCommandRepository = new FakeReviewRepository();
		reviewCommandRepository.save(review);
		ReviewService reviewService = new ReviewService(reviewCommandRepository, null, null);

		//when
		Review update = reviewService.updateReview(1L, context);

		//then
		assertThat(update.getContent()).isEqualTo("new content");
		assertThat(update.getStar()).isEqualTo(3.5D);
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
			.userId(1L)
			.itemId(1L)
			.build();

		UserFindHelper userFindHelper = mock(UserFindHelper.class);
		ItemFindHelper itemFindHelper = mock(ItemFindHelper.class);

		User user = FixtureMonkeyFactory.get()
			.giveMeBuilder(User.class)
			.set("id", 1L)
			.set("reviewIds", new HashSet<>())
			.sample();
		user.getReviewIds().add(new ReviewId(1L));

		Item item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information", null)
			.set("reviewIds", new HashSet<>())
			.set("statistics.starAvg", 4.5D)
			.set("statistics.reviewCount", 1)
			.sample();

		item.getReviewIds().add(new ReviewId(1L));

		when(userFindHelper.findUser(anyLong())).thenReturn(user);
		when(itemFindHelper.findItem(anyLong())).thenReturn(item);

		FakeReviewRepository reviewCommandRepository = new FakeReviewRepository();
		reviewCommandRepository.save(review);
		ReviewService reviewService = new ReviewService(reviewCommandRepository, itemFindHelper, userFindHelper);

		//when
		reviewService.deleteReview(1L, context);

		//then
		assertThat(review.getReviewStatus()).isEqualByComparingTo(ReviewStatus.INACTIVE);
		assertThat(user.getReviewIds()).hasSize(0);
		assertThat(item.getReviewIds()).hasSize(0);
		assertThat(item.getStatistics().getReviewCount()).isEqualTo(0);
	}

}