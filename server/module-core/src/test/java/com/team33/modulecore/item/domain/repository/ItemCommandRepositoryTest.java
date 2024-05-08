package com.team33.modulecore.item.domain.repository;

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
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.mock.FakeItemCommandRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemCommandRepositoryTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private ItemCommandRepository itemCommandRepository;

	@BeforeAll
	void beforeAll() {
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
		em.getTransaction().begin();
		itemCommandRepository = new FakeItemCommandRepository(em);

	}

	@AfterAll
	void afterAll() {
		em.getTransaction().rollback(); // 커넥션 반납용 롤백
		em.close();
		emf.close();
	}

	@DisplayName("조회수를 늘릴 수 있다.")
	@Test
	void 조회수_증가() throws Exception {
		//given
		Item item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", null)
			.set("categoryNames", null)
			.set("statistics.view", 0L)
			.set("information.price.discountPrice", 1)
			.set("categories", null)
			.sample();

		em.persist(item);

		//when
		Item item1 = itemCommandRepository.incrementView(item.getId());

		//then
		assertThat(item1.getStatistics().getView()).isEqualTo(1L);
	}
}