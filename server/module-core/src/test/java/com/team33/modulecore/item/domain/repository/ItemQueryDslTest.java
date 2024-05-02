package com.team33.modulecore.item.domain.repository;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.MockEntityFactory;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.ItemPageDto;
import com.team33.modulecore.item.dto.ItemPageRequestDto;
import com.team33.modulecore.item.dto.ItemPriceRequstDto;
import com.team33.modulecore.item.dto.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.infra.ItemQueryDslDao;
import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Page;

@TestInstance(Lifecycle.PER_CLASS)
class ItemQueryDslTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private ItemQueryRepository itemQueryRepository;


    @BeforeAll
    void beforeAll() {
        emf = Persistence.createEntityManagerFactory("test");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        itemQueryRepository = new ItemQueryDslDao(new JPAQueryFactory(em));
//        getMockItem();
        MockEntityFactory mockEntityFactory = MockEntityFactory.of(em);
        mockEntityFactory.persistItem();
    }

    @AfterAll
    void afterAll() {
        em.getTransaction().rollback(); // 커넥션 반납용 롤백
        em.close();
        emf.close();
    }


    @DisplayName("판매량이 높은 9개를 조회할 수 있다.")
    @Test
    void 판매량_9() throws Exception {
        //given
        //when
        List<Item> itemsWithSalesTop9 = itemQueryRepository.findItemsWithSalesTop9();

        //then
        assertThat(itemsWithSalesTop9).hasSize(9)
            .isSortedAccordingTo(comparing(Item::getSales).reversed())
            .extracting("title")
            .containsExactly("title15",
                "title14",
                "title13",
                "title12",
                "title11",
                "title10",
                "title9",
                "title8",
                "title7"
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
            .isSortedAccordingTo(comparing(Item::getDiscountRate).reversed())
            .extracting("title")
            .containsExactly("title15",
                "title14",
                "title13",
                "title12",
                "title11",
                "title10",
                "title9",
                "title8",
                "title7"
            );
    }

    @DisplayName("세일 중인 아이템을 조회할 수 있다.")
    @Test
    void 세일_아이템_조회() throws Exception {
        //given
        var dto = new ItemPageRequestDto();
        dto.setPage(1);
        dto.setSize(14);

        //when
        Page<ItemQueryDto> items = itemQueryRepository.findItemOnSale(
            ItemPageDto.to(dto)
        );
        //then
        assertThat(items.getContent()).hasSize(15)
            .isSortedAccordingTo(comparing(ItemQueryDto::getSales).reversed());
    }


    @Nested
    @DisplayName("아이템 정렬 타입에 따른 정렬 테스트")
    class ItemTitleQuerytest {

        @DisplayName("이름이 포함된 아이템을 조회할 수 있다.")
        @Test
        void 아이템_이름_조회() throws Exception {
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(14);

            //when
            Page<ItemQueryDto> tes = itemQueryRepository.findFilteredItems(
                "tit",
                new PriceFilterDto(),
                ItemPageDto.to(dto)
            );

            //then
            assertThat(tes.getContent())
                .hasSize(15)
                .extracting("title")
                .contains("title12", Index.atIndex(3));
            assertThat(tes.getSize()).isEqualTo(16);
            assertThat(tes.getTotalElements()).isEqualTo(15);
            assertThat(tes.getTotalPages()).isEqualTo(1);
        }

        @DisplayName("이름을 통해 조회된 아이템들을 판매량 순으로 정렬할 수 있다.")
        @Test
        void 판매량_순() throws Exception {
            //given
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(14);
            dto.setSortOption(ItemSortOption.SALES);

            //when
            Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
                "title",
                new PriceFilterDto(),
                ItemPageDto.to(dto)
            );

            //then
            assertThat(items.getContent())
                .hasSize(15)
                .isSortedAccordingTo(comparing(ItemQueryDto::getSales).reversed());

            assertThat(items.getTotalElements()).isEqualTo(15);
            assertThat(items.getTotalPages()).isEqualTo(1);
        }

        @DisplayName("이름을 통해 조회된 아이템들을 할인율 순으로 내림차순 할 수 있다.")
        @Test
        void 할인율_순() throws Exception {
            //given
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(14);
            dto.setSortOption(ItemSortOption.DISCOUNT_RATE_H);

            //when
            Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
                "title",
                new PriceFilterDto(),
                ItemPageDto.to(dto)
            );

            //then
            assertThat(items.getContent())
                .hasSize(15)
                .isSortedAccordingTo(comparing(ItemQueryDto::getDiscountRate).reversed());

            assertThat(items.getTotalElements()).isEqualTo(15);
            assertThat(items.getTotalPages()).isEqualTo(1);
        }

        @DisplayName("이름을 통해 조회된 아이템들을 가격순으로 오름차순 정렬할 수 있다.")
        @Test
        void 최저가_순() throws Exception {
            //given
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(14);
            dto.setSortOption(ItemSortOption.PRICE_L);

            //when
            Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
                "title",
                new PriceFilterDto(),
                ItemPageDto.to(dto)
            );

            //then
            assertThat(items.getContent())
                .hasSize(15)
                .isSortedAccordingTo(comparing(ItemQueryDto::getRealPrice));

            assertThat(items.getTotalElements()).isEqualTo(15);
            assertThat(items.getTotalPages()).isEqualTo(1);
        }

        @DisplayName("이름을 통해 조회된 아이템들을 가격순으로 내림차순 정렬할 수 있다.")
        @Test
        void 최고가_순() throws Exception {
            //given
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(14);
            dto.setSortOption(ItemSortOption.PRICE_H);

            //when
            Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
                "title",
                new PriceFilterDto(),
                ItemPageDto.to(dto)
            );

            //then
            assertThat(items.getContent())
                .hasSize(15)
                .isSortedAccordingTo(comparing(ItemQueryDto::getRealPrice).reversed());

            assertThat(items.getTotalElements()).isEqualTo(15);
            assertThat(items.getTotalPages()).isEqualTo(1);

        }
    }

    @Nested
    @DisplayName("가격 따른 필터링 테스트(1_000원 ~ 15_000원)")
    class PriceFilterTest {

        @DisplayName("가격이 1 만원에서 1만 5000원 필터링 조회 -> 높은 가격순")
        @Test
        void 가격_필터링1() throws Exception {
            //given
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(16);
            dto.setSortOption(ItemSortOption.PRICE_H);

            //when
            Page<ItemQueryDto> itemsByPrice =
                itemQueryRepository.findFilteredItems(
                    null,
                    PriceFilterDto.to(new ItemPriceRequstDto(10000, 15000)),
                    ItemPageDto.to(dto)
                );

            //then
            assertThat(itemsByPrice.getContent()).hasSize(6)
                .isSortedAccordingTo(Comparator.comparing(ItemQueryDto::getRealPrice).reversed())
                .extracting("itemId", "realPrice")
                .containsExactlyInAnyOrder(
                    tuple(10L, 10000),
                    tuple(11L, 11000),
                    tuple(12L, 12000),
                    tuple(13L, 13000),
                    tuple(14L, 14000),
                    tuple(15L, 15000)
                );

            assertThat(itemsByPrice.getTotalElements()).isEqualTo(6);
            assertThat(itemsByPrice.getTotalPages()).isEqualTo(1);
            assertThat(itemsByPrice.getNumberOfElements()).isEqualTo(6);
        }

        @DisplayName("가격이  1천원에서 1만 4999원 필터링 조회 -> 낮은 가격순")
        @Test
        void 가격_필터링2() throws Exception {
            //given
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(10);
            dto.setSortOption(ItemSortOption.PRICE_L);

            //when
            Page<ItemQueryDto> itemsByPrice =
                itemQueryRepository.findFilteredItems(
                    null,
                    PriceFilterDto.to(new ItemPriceRequstDto(1000, 14999)),
                    ItemPageDto.to(dto)
                );

            //then
            assertThat(itemsByPrice.getContent()).hasSize(14)
                .isSortedAccordingTo(Comparator.comparing(ItemQueryDto::getRealPrice))
                .extracting("itemId")
                .doesNotContain(15L);

            assertThat(itemsByPrice.getTotalElements()).isEqualTo(14);
            assertThat(itemsByPrice.getTotalPages()).isEqualTo(1);
            assertThat(itemsByPrice.getNumberOfElements()).isEqualTo(14);
        }

        @DisplayName("특정 가격 조회 시 아이템을 조회할 수 있다.")
        @Test
        void 가격_필터링3() throws Exception {
            //given
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(10);
            dto.setSortOption(ItemSortOption.PRICE_L);

            //when
            Page<ItemQueryDto> itemsByPrice =
                itemQueryRepository.findFilteredItems(
                    null,
                    PriceFilterDto.to(new ItemPriceRequstDto(1000, 1000)),
                    ItemPageDto.to(dto)
                );

            //then
            assertThat(itemsByPrice.getContent()).hasSize(1)
                .isSortedAccordingTo(Comparator.comparing(ItemQueryDto::getRealPrice))
                .extracting("itemId")
                .containsExactly(1L);

            assertThat(itemsByPrice.getTotalElements()).isEqualTo(1);
            assertThat(itemsByPrice.getTotalPages()).isEqualTo(1);
            assertThat(itemsByPrice.getNumberOfElements()).isEqualTo(1);
        }

        @DisplayName("특정 가격 조회 시 아이템이 없을 경우 예외를 던진다.")
        @Test
        void 가격_필터링4() throws Exception {
            //given
            var dto = new ItemPageRequestDto();
            dto.setPage(1);
            dto.setSize(10);
            dto.setSortOption(ItemSortOption.PRICE_L);

            //when//then
            assertThatThrownBy(() ->
                itemQueryRepository.findFilteredItems(
                    null,
                    PriceFilterDto.to(new ItemPriceRequstDto(11111, 11111)),
                    ItemPageDto.to(dto)
                )
            )
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining(ExceptionCode.ITEM_NOT_FOUND.getMessage());
        }
    }

}