package com.team33.modulecore.item.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.EntityManagerSetting;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.mock.FakeItemCommandRepository;
import java.util.ArrayList;
import java.util.HashSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * {@linkplain FakeItemCommandRepository}는 테스트용 가짜 레포지토리입니다.
 */
@TestInstance(Lifecycle.PER_CLASS)
public class ItemCommandEntityManagerTest extends EntityManagerSetting {

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

    public

    @DisplayName("item을 저장할 수 있다.")
    @Test
    void 아이템_저장() throws Exception {
        //given
        Item item = getItem();

        //when
        Item save = itemCommandRepository.save(item);

        //then
        assertThat(save.getId()).isNotNull();
    }


    @DisplayName("item을 id로 조회할 수 있다.")
    @Test
    void 아이템_조회() throws Exception {
        //given
        Item item = getItem();

        Item save = itemCommandRepository.save(item);

        //when
        Item result = itemCommandRepository.findById(save.getId()).get();

        //then
        assertThat(result).isSameAs(item);
    }

    private Item getItem() {
        FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
            .builder()
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .defaultNotNull(true)
            .build();

        return FIXTURE_MONKEY.giveMeBuilder(Item.class).set("id", null)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("itemCategories", new HashSet<>())
            .sample();
    }
}
