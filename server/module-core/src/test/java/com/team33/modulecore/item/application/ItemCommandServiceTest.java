package com.team33.modulecore.item.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.review.domain.entity.Review;

class ItemCommandServiceTest {

	@DisplayName("아이템 조회수를 업데이트 할 수 있다.")
	@Test
	void 아이템_조회수_증가() throws Exception {
		//given
		ItemCommandRepository itemCommandRepository = mock(ItemCommandRepository.class);
		doNothing().when(itemCommandRepository).incrementView(anyLong());

		ItemCommandService itemCommandService = new ItemCommandService(itemCommandRepository);

		//when
		itemCommandService.getAndIncreaseView(1L);

		//then
		verify(itemCommandRepository, times(1)).incrementView(anyLong());
	}

	@DisplayName("아이템 판매량을 1 증가시켜 업데이트합니다.")
	@Test
	void 아이템_판매량_증가() throws Exception {
		//given
		ItemCommandRepository itemCommandRepository = mock(ItemCommandRepository.class);
		when(itemCommandRepository.incrementSales(anyLong())).thenReturn(1);

		ItemCommandService itemCommandService = new ItemCommandService(itemCommandRepository);

		//when
		itemCommandService.addSales(List.of(1L, 2L, 3L));

		//then
		verify(itemCommandRepository, times(3)).incrementSales(anyLong());
	}

	@DisplayName("리뷰 아이디를 추가할 수 있다.")
	@Test
	void 리뷰_아이디_추가() throws Exception {
		//given
		Item item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("itemCategory", null)
			.set("categories", null)
			.set("reviewIds", new HashSet<>())
			.set("statistics.reviewCount", 0)
			.set("statistics.starAvg", 0D)
			.sample();

		ItemCommandRepository itemCommandRepository = mock(ItemCommandRepository.class);
		when(itemCommandRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
		ItemCommandService itemCommandService = new ItemCommandService(itemCommandRepository);

		//when
		itemCommandService.addReviewId(1L, 1L, 5.0);

		//then
		verify(itemCommandRepository, times(1)).findById(anyLong());

		assertThat(item.getStarAvg()).isEqualTo(5.0);
		assertThat(item.getStatistics().getReviewCount()).isEqualTo(1);
		assertThat(item.getReviewIds()).hasSize(1);
	}

	@DisplayName("리뷰 아이디를 추가할 수 있다. => 2이상")
	@Test
	void 리뷰_아이디_추가2() throws Exception {
		//given
		Item item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("itemCategory", null)
			.set("categories", null)
			.set("reviewIds", new HashSet<>())
			.set("statistics.reviewCount", 0)
			.set("statistics.starAvg", 0D)
			.sample();

		item.addReviewId(2L);
		item.updateCountAndStars(4.0);
		ItemCommandRepository itemCommandRepository = mock(ItemCommandRepository.class);
		when(itemCommandRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
		ItemCommandService itemCommandService = new ItemCommandService(itemCommandRepository);

		//when
		itemCommandService.addReviewId(1L, 1L, 5.0);


		//then
		verify(itemCommandRepository, times(1)).findById(anyLong());

		assertThat(item.getStarAvg()).isEqualTo(4.5);
		assertThat(item.getStatistics().getReviewCount()).isEqualTo(2);
		assertThat(item.getReviewIds()).hasSize(2);
	}

	@DisplayName("리뷰 아이디를 삭제할 수 있다. => 1개")
	@Test
	void 리뷰_아이디_삭제() throws Exception {
		//given
		Item item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("itemCategory", null)
			.set("categories", null)
			.set("reviewIds", new HashSet<>())
			.set("statistics.reviewCount", 0)
			.set("statistics.starAvg", 0D)
			.sample();

		item.addReviewId(1L);
		item.updateCountAndStars(5.0);

		Review review = FixtureMonkeyFactory.get().giveMeBuilder(Review.class)
			.set("id", 1L)
			.set("star", 5.0)
			.sample();

		ItemCommandRepository itemCommandRepository = mock(ItemCommandRepository.class);
		when(itemCommandRepository.findById(anyLong())).thenReturn(Optional.of(item));
		ItemCommandService itemCommandService = new ItemCommandService(itemCommandRepository);

		//when
		itemCommandService.deleteReviewId(1L, review);

		//then
		verify(itemCommandRepository, times(1)).findById(anyLong());

		assertThat(item.getStarAvg()).isEqualTo(0.0);
		assertThat(item.getStatistics().getReviewCount()).isEqualTo(0);
		assertThat(item.getReviewIds()).hasSize(0);

	}

	@DisplayName("리뷰 아이디를 삭제할 수 있다. => 2개 이상")
	@Test
	void 리뷰_아이디_삭제2() throws Exception {
		//given
		Item item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("itemCategory", null)
			.set("categories", null)
			.set("reviewIds", new HashSet<>())
			.set("statistics.reviewCount", 0)
			.set("statistics.starAvg", 0D)
			.sample();

		item.addReviewId(1L);
		item.addReviewId(2L);
		item.updateCountAndStars(5.0);
		item.updateCountAndStars(4.0);

		Review review = FixtureMonkeyFactory.get().giveMeBuilder(Review.class)
			.set("id", 1L)
			.set("star", 5.0)
			.sample();

		ItemCommandRepository itemCommandRepository = mock(ItemCommandRepository.class);
		when(itemCommandRepository.findById(anyLong())).thenReturn(Optional.of(item));
		ItemCommandService itemCommandService = new ItemCommandService(itemCommandRepository);

		//when
		itemCommandService.deleteReviewId(1L, review);

		//then
		verify(itemCommandRepository, times(1)).findById(anyLong());

		assertThat(item.getStarAvg()).isEqualTo(4.0);
		assertThat(item.getStatistics().getReviewCount()).isEqualTo(1);
		assertThat(item.getReviewIds()).hasSize(1);

	}
}
