package com.team33.modulecore.order.domain.repository;

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
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.mock.FakeOrderCommandRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderCommandRepositoryTest {
	private EntityManagerFactory emf;
	private EntityManager em;
	private OrderCommandRepository orderCommandRepository;

	@BeforeAll
	void beforeAll() {
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
		orderCommandRepository = new FakeOrderCommandRepository(em);
		em.getTransaction().begin();
	}

	@AfterAll
	void afterAll() {
		em.getTransaction().rollback(); // 커넥션 반납용 롤백
		em.close();
		emf.close();
	}

	@DisplayName("구독 정보를 조회할 수 있다.")
	@Test
	void 구독_정보_조회() throws Exception {
		//given
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.setNull("user")
			.set("isSubscription", true)
			.sample();

		em.persist(order);

		//when
		boolean isSubscriptionById = orderCommandRepository.findIsSubscriptionById(order.getId());

		//then
		assertThat(isSubscriptionById).isTrue();
	}

	@DisplayName("tid를 조회할 수 있다.")
	@Test
	void tid_조회() throws Exception {

		//given
		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.setNull("user")
			.set("paymentCode.tid", "tid").sample();

		em.persist(order);

		//when
		String tid = orderCommandRepository.findTid(order.getId());

		//then
		assertThat(tid).isEqualTo("tid");
	}

}