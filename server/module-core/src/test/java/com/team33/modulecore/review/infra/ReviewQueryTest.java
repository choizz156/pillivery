package com.team33.modulecore.review.infra;

import static java.util.Comparator.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Page;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.review.domain.entity.Review;
import com.team33.modulecore.review.dto.query.ReviewPage;
import com.team33.modulecore.review.dto.query.ReviewQueryDto;
import com.team33.modulecore.review.dto.query.ReviewSortOption;
import com.team33.modulecore.review.repository.ReviewQueryRepository;

@TestInstance(Lifecycle.PER_CLASS)
class ReviewQueryTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private ReviewQueryRepository reviewQueryRepository;

	@BeforeAll
	void beforeAll() {
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
		em.getTransaction().begin();
		reviewQueryRepository = new ReviewQueryDslDao(new JPAQueryFactory(em));
		persistReview();
	}

	@AfterAll
	void afterAll() {
		em.getTransaction().rollback(); // 커넥션 반납용 롤백
		em.close();
		emf.close();
	}

	@DisplayName("특정 아이템의 리뷰를 조회할 수 있다")
	@Test
	void 리뷰_조회1() throws Exception {
		//given
		ReviewPage reviewPage = ReviewPage.builder()
			.page(1)
			.size(14)
			.sortOption(ReviewSortOption.NEWEST)
			.build();

		//when
		Page<ReviewQueryDto> reviews = reviewQueryRepository.findByItemId(1L, reviewPage);

		//then
		List<ReviewQueryDto> content = reviews.getContent();
		assertThat(content).hasSize(10)
			.isSortedAccordingTo(comparing(ReviewQueryDto::getCreatedAt).reversed())
			.extracting(
				"reviewId",
				"itemId",
				"userId",
				"content",
				"displayName",
				"star",
				"createdAt",
				"updatedAt",
				"reviewStatus"
			)
			.doesNotContainNull();
	}

	@DisplayName("특정 유저의 리뷰를 조회할 수 있다.")
	@Test
	void 리뷰_조회2() throws Exception {
		//given
		ReviewPage reviewPage = ReviewPage.builder()
			.page(1)
			.size(14)
			.sortOption(ReviewSortOption.NEWEST)
			.build();

		//when
		Page<ReviewQueryDto> reviews = reviewQueryRepository.findByUserId(1L, reviewPage);

		//then
		List<ReviewQueryDto> content = reviews.getContent();
		assertThat(content).hasSize(10)
			.isSortedAccordingTo(comparing(ReviewQueryDto::getCreatedAt).reversed())
			.extracting(
				"reviewId",
				"itemId",
				"userId",
				"content",
				"displayName",
				"star",
				"createdAt",
				"updatedAt",
				"reviewStatus"
			)
			.doesNotContainNull();
	}

	@DisplayName("특정 아이템의 리뷰가 없는 경우 빈 페이지를 리턴한다.")
	@Test
	void 리뷰_예외() throws Exception {
		//given
		ReviewPage reviewPage = ReviewPage.builder()
			.page(1)
			.size(14)
			.sortOption(ReviewSortOption.NEWEST)
			.build();

		//when
		Page<ReviewQueryDto> reviews = reviewQueryRepository.findByItemId(2L, reviewPage);

		//then
		assertThat(reviews).isEmpty();
	}

	@DisplayName("특정 유저가 작성한 리뷰가 없는 경우 빈 페이지를 리턴한다.")
	@Test
	void 리뷰_예외2() throws Exception {
		//given
		ReviewPage reviewPage = ReviewPage.builder()
			.page(1)
			.size(14)
			.sortOption(ReviewSortOption.NEWEST)
			.build();

		//when
		Page<ReviewQueryDto> reviews = reviewQueryRepository.findByUserId(2L, reviewPage);

		//then
		assertThat(reviews).isEmpty();
	}

	@Nested
	@DisplayName("리뷰 정렬")
	class ReviewSort {

		@DisplayName("오래된 순")
		@Test
		void 정렬1() throws Exception {
			ReviewPage reviewPage = ReviewPage.builder()
				.page(1)
				.size(14)
				.sortOption(ReviewSortOption.OLDEST)
				.build();

			//when
			Page<ReviewQueryDto> reviews = reviewQueryRepository.findByItemId(1L, reviewPage);

			//then
			List<ReviewQueryDto> content = reviews.getContent();
			assertThat(content).hasSize(10)
				.isSortedAccordingTo(comparing(ReviewQueryDto::getCreatedAt));
		}

		@DisplayName("별점 높은 순")
		@Test
		void 정렬2() throws Exception {
			ReviewPage reviewPage = ReviewPage.builder()
				.page(1)
				.size(14)
				.sortOption(ReviewSortOption.STAR_H)
				.build();

			//when
			Page<ReviewQueryDto> reviews = reviewQueryRepository.findByItemId(1L, reviewPage);

			//then
			List<ReviewQueryDto> content = reviews.getContent();
			assertThat(content).hasSize(10)
				.isSortedAccordingTo(comparing(ReviewQueryDto::getStar).reversed());
		}

		@DisplayName("별점 낮은 순")
		@Test
		void 정렬3() throws Exception {
			ReviewPage reviewPage = ReviewPage.builder()
				.page(1)
				.size(14)
				.sortOption(ReviewSortOption.STAR_L)
				.build();

			//when
			Page<ReviewQueryDto> reviews = reviewQueryRepository.findByItemId(1L, reviewPage);

			//then
			List<ReviewQueryDto> content = reviews.getContent();
			assertThat(content).hasSize(10)
				.isSortedAccordingTo(comparing(ReviewQueryDto::getStar));
		}
	}

	private void persistReview() {
		AtomicReference<Double> value = new AtomicReference<>(0D);
		List<Review> reviews = FixtureMonkeyFactory.get().giveMeBuilder(Review.class)
			.setNull("id")
			.set("itemId", 1L)
			.set("userId", 1L)
			.setLazy("star", () -> value.getAndSet(getNewValue(value)))
			.sampleList(10);

		reviews.forEach(em::persist);
	}

	private double getNewValue(AtomicReference<Double> value) {
		return value.get() + 1D > 5D ? 5D : value.getAndSet(value.get() + 1D);
	}
}