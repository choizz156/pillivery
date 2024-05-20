package com.team33.modulecore.item.domain.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.mock.FakeItemCommandRepository;

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

	@BeforeEach
	void setUp() {
		em.clear();
	}

	@DisplayName("조회수를 늘릴 수 있다.")
	@Test
	void 조회수_증가() throws Exception {
		//given
		Item item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", null)
			.set("itemCategory", null)
			.set("statistics.view", 0L)
			.set("information.price.discountPrice", 1)
			.set("categories", null)
			.sample();

		em.persist(item);

		//when
		Item updateItem = itemCommandRepository.incrementView(item.getId());

		//then
		assertThat(updateItem.getStatistics().getView()).isEqualTo(1L);
	}

	@DisplayName("아이템 판매량을 증가시킬 수 있다.")
	@Test
	void 아이템_판매량_증가() throws Exception {
		//given
		Item item = FixtureMonkeyFactory.get()
			.giveMeBuilder(Item.class)
			.set("id", null)
			.set("itemCategory", null)
			.set("statistics.sales", 0)
			.set("categories", null)
			.set("reviewIds", new HashSet<>())
			.sample();

		em.persist(item);

		//when
		int updateItemId = itemCommandRepository.incrementSales(item.getId());

		//then
		Item updateItem = em.find(Item.class, updateItemId);
		assertThat(updateItem.getStatistics().getSales()).isEqualTo(1L);
	}
}