package com.team33.modulecore.review.domain.repository;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.review.domain.entity.Review;
import com.team33.modulecore.core.review.domain.repository.ReviewCommandRepository;
import com.team33.modulecore.review.domain.mock.FakeReviewCommandRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewCommandRepositoryTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private ReviewCommandRepository reviewCommandRepository;

	@BeforeAll
	void beforeAll() {
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
		reviewCommandRepository = new FakeReviewCommandRepository(em);
		em.getTransaction().begin();
	}

	@AfterAll
	void afterAll() {
		em.getTransaction().rollback(); // 커넥션 반납용 롤백
		em.close();
		emf.close();
	}

	@DisplayName("중복된 리뷰를 찾을 수 있다.")
	@Test
	void 중복_리뷰() throws Exception{
		//given
		Review review = FixtureMonkeyFactory.get().giveMeBuilder(Review.class)
			.setNull("id")
			.set("userId", 1L)
			.set("itemId", 1L)
			.sample();

		em.persist(review);
		//when
		boolean duplicated = reviewCommandRepository.findDuplicated(1L, 1L);

		//then
		assertThat(duplicated).isTrue();
	}

}