package com.team33.modulecore.review.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.user.domain.User;

class ReviewTest {

	@DisplayName("review 객체를 생성할 수 있다.")
	@Test
	void 리뷰_생성() throws Exception {
		//given
		User user = FixtureMonkeyFactory.get().giveMeBuilder(User.class)
			.set("id", 1L)
			.set("reviewIds", new HashSet<>())
			.sample();
		Item item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information.price.realPrice", 1000)
			.set("information.price.discountPrice", 500)
			.sample();

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

}