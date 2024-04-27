package com.team33.modulecore.item.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.ItemPageDto;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;


class ItemQueryRepositoryTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static MemoryItemRepository itemQueryRepository;

    private static FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @BeforeAll
    static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("test");
        em = emf.createEntityManager();
        itemQueryRepository = new MemoryItemRepository(em);
        em.getTransaction().begin();

        var items = toSavedItems();

        itemQueryRepository.saveAll(items);
    }

    @AfterAll
    static void afterAll() {
        em.getTransaction().rollback(); // 커넥션 반납용 롤백
        em.close();
        emf.close();

        em = null;
        emf = null;
        FIXTURE_MONKEY = null;
    }

    @DisplayName("판매량이 높은 9개를 조회할 수 있다.")
    @Test
    void 판매량_9() throws Exception {
        //given
        //when
        List<Item> itemsWithSalesTop9 = itemQueryRepository.findItemsWithSalesTop9();

        //then
        assertThat(itemsWithSalesTop9).hasSize(9)
            .isSortedAccordingTo(Comparator.comparing(Item::getSales).reversed())
            .extracting("title")
            .containsExactly("title16",
                "title15",
                "title14",
                "title13",
                "title12",
                "title11",
                "title10",
                "title9",
                "title8"
            );
    }

    @DisplayName("할인율이 높은 9개를 조회할 수 있다.")
    @Test
    void 할인율_9() throws Exception {
        //given
        //when
        List<Item> top9SaleItems = itemQueryRepository.findItemsWithDiscountRateTop9();

        //then
        assertThat(top9SaleItems).hasSize(9)
            .isSortedAccordingTo(Comparator.comparing(Item::getDiscountRate).reversed())
            .extracting("title")
            .containsExactly("title16",
                "title15",
                "title14",
                "title13",
                "title12",
                "title11",
                "title10",
                "title9",
                "title8"
            );
    }

    @DisplayName("아이템 이름을 통해 조회할 수 있다.")
    @Test
    void 아이템_이름_조회() throws Exception {

        var dto = new ItemPageDto();
        dto.setPage(1);
        dto.setSize(14);

        //when
        Page<Item> items = itemQueryRepository.findByTitle("test", ItemSearchRequest.to(dto));

        //then
        assertThat(items.getContent())
            .hasSize(3)
            .isSortedAccordingTo(Comparator.comparing(Item::getSales).reversed())
            .extracting("title")
            .containsExactlyInAnyOrder("test12", "test11", "test10");
        assertThat(items.getSize()).isEqualTo(16);
        assertThat(items.getTotalElements()).isEqualTo(3);
        assertThat(items.getTotalPages()).isEqualTo(1);
    }

    private static List<Item> toSavedItems() {
        var value = new AtomicInteger(1);
        var value1 = new AtomicReference<Double>(1D);
        var items = FIXTURE_MONKEY.giveMeBuilder(Item.class)
            .set("id", null)
            .setLazy("sales", () -> value.addAndGet(1))
            .setLazy("itemPrice.discountRate", () -> value1.getAndSet(value1.get() + 1D))
            .setLazy("title", () -> "title" + value)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("categories", new ArrayList<>())
            .sampleList(15);

        var value2 = new AtomicInteger(9);
        var items2 = FIXTURE_MONKEY.giveMeBuilder(Item.class)
            .set("id", null)
            .set("sales", 0)
            .set("itemPrice.discountRate", 0)
            .setLazy("title", () -> "test" + value2.addAndGet(1))
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("categories", new ArrayList<>())
            .sampleList(3);

        items.addAll(items2);
        return items;
    }
}