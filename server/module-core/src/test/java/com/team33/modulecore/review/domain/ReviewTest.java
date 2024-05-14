package com.team33.modulecore.review.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReviewTest {

	private Review review;

	@BeforeEach
	void setUp() {
		review = Review.create(
			ReviewContext.builder()
				.content("content")
				.star(5)
				.userId(1L)
				.itemId(1L)
				.build()
		);
	}

	@DisplayName("review 객체를 생성할 수 있다.")
	@Test
	void 리뷰_생성() throws Exception {
		//given
		//when
		Review review = Review.create(
			ReviewContext.builder()
				.content("content")
				.star(5)
				.userId(1L)
				.itemId(1L)
				.build()
		);

		//then
		assertThat(review.getUserId()).isEqualTo(1L);
		assertThat(review.getItemId()).isEqualTo(1L);
		assertThat(review.getContent()).isEqualTo("content");
		assertThat(review.getStar()).isEqualTo(5);
	}

	@DisplayName("리뷰를 수정할 수 있다.")
	@Test
	void 리뷰_수정() throws Exception {
		//given
		ReviewContext newContent = ReviewContext.builder()
			.content("new content")
			.star(3.5)
			.itemId(1L)
			.userId(1L)
			.build();

		//when
		Review update = review.update(newContent);

		//then
		assertThat(update.getContent()).isEqualTo("new content");
		assertThat(update.getStar()).isEqualTo(3.5);
	}

	@DisplayName("리뷰를 수정시 작성자가 다르다면 예외를 던진다.")
	@Test
	void 리뷰_예외() throws Exception {
		//given

		ReviewContext newContent = ReviewContext.builder()
			.content("new content")
			.star(3.5)
			.itemId(1L)
			.userId(2L)
			.build();

		//when
		//then
		assertThatThrownBy(() -> review.update(newContent))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("리뷰를 수정시 아이템이 다르면 예외를 던진다.")
	@Test
	void 리뷰_예외2() throws Exception {
		//given
		ReviewContext newContent = ReviewContext.builder()
			.content("new content")
			.star(3.5)
			.itemId(2L)
			.userId(1L)
			.build();

		//when
		//then
		assertThatThrownBy(() -> review.update(newContent))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("리뷰 삭제 시 리뷰의 상태를 변경한다.")
	@Test
	void 리뷰_삭제() throws Exception {
		//given
		ReviewContext context = ReviewContext.builder()
			.itemId(1L)
			.userId(1L)
			.build();

		//when
		review.delete(context);

		//then
		assertThat(review.getReviewStatus()).isEqualByComparingTo(ReviewStatus.INACTIVE);
	}

}