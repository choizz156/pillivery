//package com.team33.modulecore.item.domain.repository;
//
//import static java.util.Comparator.comparing;
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.navercorp.fixturemonkey.FixtureMonkey;
//import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.team33.modulecore.item.domain.Brand;
//import com.team33.modulecore.item.domain.ItemSortOption;
//import com.team33.modulecore.item.domain.entity.Item;
//import com.team33.modulecore.item.dto.ItemPageDto;
//import com.team33.modulecore.item.dto.ItemSearchRequest;
//import com.team33.modulecore.item.infra.ItemQueryRepositoryImpl;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicReference;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.springframework.data.domain.Page;
//
//@TestInstance(Lifecycle.PER_CLASS)
//class ItemQueryRepositoryTest {
//
//    private EntityManagerFactory emf;
//    private EntityManager em;
//    private ItemQueryRepository itemQueryRepository;
//
//
//    @BeforeAll
//    void beforeAll() {
//        emf = Persistence.createEntityManagerFactory("test");
//        em = emf.createEntityManager();
//        em.getTransaction().begin();
//        itemQueryRepository = new ItemQueryRepositoryImpl(new JPAQueryFactory(em));
//        getMockItem();
//    }
//
//    @AfterAll
//    void afterAll() {
//        em.getTransaction().rollback(); // 커넥션 반납용 롤백
//        em.close();
//        emf.close();
//    }
//
//
//    @DisplayName("판매량이 높은 9개를 조회할 수 있다.")
//    @Test
//    void 판매량_9() throws Exception {
//        //given
//        //when
//        List<Item> itemsWithSalesTop9 = itemQueryRepository.findItemsWithSalesTop9();
//
//        //then
//        assertThat(itemsWithSalesTop9).hasSize(9)
//            .isSortedAccordingTo(comparing(Item::getSales).reversed())
//            .extracting("title")
//            .containsExactly("title16",
//                "title15",
//                "title14",
//                "title13",
//                "title12",
//                "title11",
//                "title10",
//                "title9",
//                "title8"
//            );
//    }
//
//    @DisplayName("할인율이 높은 9개를 조회할 수 있다.")
//    @Test
//    void 할인율_9() throws Exception {
//        //given
//        //when
//        List<Item> top9SaleItems = itemQueryRepository.findItemsWithDiscountRateTop9();
//
//        //then
//        assertThat(top9SaleItems).hasSize(9)
//            .isSortedAccordingTo(comparing(Item::getDiscountRate).reversed())
//            .extracting("title")
//            .containsExactly("title16",
//                "title15",
//                "title14",
//                "title13",
//                "title12",
//                "title11",
//                "title10",
//                "title9",
//                "title8"
//            );
//    }
//
//
//    @Nested
//    @DisplayName("아이템 조회 테스트 및 타입 정렬 테스트")
//    class ItemTitleQuerytest {
//
//        @DisplayName("이름이 포함된 아이템을 조회할 수 있다.")
//        @Test
//        void 아이템_이름_조회() throws Exception {
//            var dto = new ItemPageDto();
//            dto.setPage(1);
//            dto.setSize(14);
//
//            //when
//            Page<Item> items = itemQueryRepository.findByTitle("tes", ItemSearchRequest.to(dto));
//
//            //then
//            assertThat(items.getContent())
//                .hasSize(3)
//                .extracting("title")
//                .containsExactlyInAnyOrder("test12", "test11", "test10");
//            assertThat(items.getSize()).isEqualTo(16);
//            assertThat(items.getTotalElements()).isEqualTo(3);
//            assertThat(items.getTotalPages()).isEqualTo(1);
//        }
//
//        @DisplayName("이름을 통해 조회된 아이템들을 판매량 순으로 정렬할 수 있다.")
//        @Test
//        void 판매량_순() throws Exception {
//            //given
//            var dto = new ItemPageDto();
//            dto.setPage(1);
//            dto.setSize(14);
//            dto.setSortOption(ItemSortOption.SALES);
//
//            //when
//            Page<Item> items = itemQueryRepository.findByTitle("title", ItemSearchRequest.to(dto));
//
//            //then
//            assertThat(items.getContent())
//                .hasSize(15)
//                .isSortedAccordingTo(comparing(Item::getSales).reversed());
//
//            assertThat(items.getTotalElements()).isEqualTo(15);
//            assertThat(items.getTotalPages()).isEqualTo(1);
//        }
//
//        @DisplayName("이름을 통해 조회된 아이템들을 할인율 순으로 내림차순 할 수 있다.")
//        @Test
//        void 할인율_순() throws Exception {
//            //given
//            var dto = new ItemPageDto();
//            dto.setPage(1);
//            dto.setSize(14);
//            dto.setSortOption(ItemSortOption.DISCOUNT_RATE_H);
//
//            //when
//            Page<Item> items = itemQueryRepository.findByTitle("title", ItemSearchRequest.to(dto));
//
//            //then
//            assertThat(items.getContent())
//                .hasSize(15)
//                .isSortedAccordingTo(comparing(Item::getDiscountRate).reversed());
//
//            assertThat(items.getTotalElements()).isEqualTo(15);
//            assertThat(items.getTotalPages()).isEqualTo(1);
//        }
//
//        @DisplayName("이름을 통해 조회된 아이템들을 가격순으로 오름차순 정렬할 수 있다.")
//        @Test
//        void 최저가_순() throws Exception {
//            //given
//            var dto = new ItemPageDto();
//            dto.setPage(1);
//            dto.setSize(14);
//            dto.setSortOption(ItemSortOption.PRICE_L);
//
//            //when
//            Page<Item> items = itemQueryRepository.findByTitle("title", ItemSearchRequest.to(dto));
//
//            //then
//            assertThat(items.getContent())
//                .hasSize(15)
//                .isSortedAccordingTo(comparing(Item::getRealPrice));
//
//            assertThat(items.getTotalElements()).isEqualTo(15);
//            assertThat(items.getTotalPages()).isEqualTo(1);
//        }
//
//        @DisplayName("이름을 통해 조회된 아이템들을 가격순으로 내림차순 정렬할 수 있다.")
//        @Test
//        void 최고가_순() throws Exception {
//            //given
//            var dto = new ItemPageDto();
//            dto.setPage(1);
//            dto.setSize(14);
//            dto.setSortOption(ItemSortOption.PRICE_H);
//
//            //when
//            Page<Item> items = itemQueryRepository.findByTitle("title", ItemSearchRequest.to(dto));
//
//            //then
//            assertThat(items.getContent())
//                .hasSize(15)
//                .isSortedAccordingTo(comparing(Item::getRealPrice).reversed());
//
//            assertThat(items.getTotalElements()).isEqualTo(15);
//            assertThat(items.getTotalPages()).isEqualTo(1);
//
//        }
//    }
//
//    private void getMockItem() {
//        FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
//            .builder()
//            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
//            .defaultNotNull(true)
//            .build();
//
//        var value = new AtomicInteger(1);
//        var value1 = new AtomicReference<Double>(1D);
//        var items = FIXTURE_MONKEY.giveMeBuilder(Item.class)
//            .set("id", null)
//            .setLazy("sales", () -> value.addAndGet(1))
//            .setLazy("itemPrice.discountRate", () -> value1.getAndSet(value1.get() + 1D))
//            .setLazy("itemPrice.realPrice", value::intValue)
//            .setLazy("title", () -> "title" + value)
//            .set("nutritionFacts", new ArrayList<>())
//            .set("reviews", null)
//            .set("brand", Brand.MYNI)
//            .set("wishList", null)
//            .set("itemCategories", new HashSet<>())
//            .sampleList(15);
//
//        var value2 = new AtomicInteger(9);
//        var items2 = FIXTURE_MONKEY.giveMeBuilder(Item.class)
//            .set("id", null)
//            .set("sales", 0)
//            .set("itemPrice.discountRate", 0)
//            .set("itemPrice.realPrice", 1)
//            .setLazy("title", () -> "test" + value2.addAndGet(1))
//            .set("nutritionFacts", new ArrayList<>())
//            .set("reviews", null)
//            .set("brand", Brand.MYNI)
//            .set("wishList", null)
//            .set("itemCategories", new HashSet<>())
//            .sampleList(3);
//
//        items.addAll(items2);
//        items.forEach(em::persist);
//    }
//}