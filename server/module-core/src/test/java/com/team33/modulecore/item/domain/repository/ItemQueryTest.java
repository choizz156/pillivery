package com.team33.modulecore.item.domain.repository;

import static com.team33.modulecore.category.domain.CategoryName.*;
import static java.util.Comparator.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.LongStream;

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

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.category.domain.Categories;
import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.dto.query.ItemPage;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilter;
import com.team33.modulecore.item.infra.ItemQueryDslDao;

@TestInstance(Lifecycle.PER_CLASS)
class ItemQueryTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private ItemQueryRepository itemQueryRepository;

	@BeforeAll
	void beforeAll() {
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
		em.getTransaction().begin();
		itemQueryRepository = new ItemQueryDslDao(new JPAQueryFactory(em));
		persistItems();
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
		List<ItemQueryDto> itemsWithSalesTop9 = itemQueryRepository.findItemsWithSalesMain();

		//then
		assertThat(itemsWithSalesTop9).hasSize(9)
			.isSortedAccordingTo(comparing(ItemQueryDto::getSales).reversed())
			.extracting("productName")
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
		List<ItemQueryDto> top9SaleItems = itemQueryRepository.findItemsWithDiscountRateMain();

		//then
		assertThat(top9SaleItems).hasSize(9)
			.isSortedAccordingTo(comparing(ItemQueryDto::getDiscountRate).reversed())
			.extracting("productName")
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
		PriceFilter priceFilter = new PriceFilter();
		ItemPage itemPage = ItemPage.builder()
			.page(1)
			.size(14)
			.build();

		//when
		Page<ItemQueryDto> items = itemQueryRepository.findItemsOnSale(
			null,
			priceFilter,
			itemPage
		);
		//then
		assertThat(items.getContent()).hasSize(14)
			.isSortedAccordingTo(comparing(ItemQueryDto::getSales).reversed());
	}

	@DisplayName("카테고리 별로 아이템을 조회할 수 있다.")
	@Test
	void 카테고리_아이템_조회() throws Exception {
		//given
		PriceFilter priceFilter = new PriceFilter();
		ItemPage itemPage = ItemPage.builder()
			.page(1)
			.size(14)
			.build();

		//when
		Page<ItemQueryDto> items = itemQueryRepository.findItemsByCategory(
			BONE,
			null,
			priceFilter,
			itemPage
		);

		//then
		assertThat(items.getContent()).hasSize(14)
			.extracting("categories.categoryNameSet")
			.containsOnly(Set.of(EYE, BONE));

	}

	@Nested
	@DisplayName("아이템 정렬 타입에 따른 정렬 테스트")
	class ItemTitleQuerytest {

		@DisplayName("이름이 포함된 아이템을 조회할 수 있다.")
		@Test
		void 아이템_이름_조회() throws Exception {
			PriceFilter priceFilter = new PriceFilter();
			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.build();

			//when
			Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
				"tit",
				priceFilter,
				itemPage
			);

			//then
			assertThat(items.getContent())
				.hasSize(14)
				.extracting("productName")
				.contains("title12", Index.atIndex(3));
			assertThat(items.getSize()).isEqualTo(14);
			assertThat(items.getTotalElements()).isEqualTo(15);
			assertThat(items.getTotalPages()).isEqualTo(2);
		}

		@DisplayName("조회된 아이템들을 판매량 순으로 정렬할 수 있다.")
		@Test
		void 판매량_순() throws Exception {
			//given
			PriceFilter priceFilter = new PriceFilter();
			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.build();

			//when
			Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
				"title",
				priceFilter,
				itemPage
			);

			//then
			assertThat(items.getContent())
				.hasSize(14)
				.isSortedAccordingTo(comparing(ItemQueryDto::getSales).reversed());

			assertThat(items.getTotalElements()).isEqualTo(15);
			assertThat(items.getTotalPages()).isEqualTo(2);
		}

		@DisplayName("조회된 아이템들을 할인율 순으로 내림차순 할 수 있다.")
		@Test
		void 할인율_순() throws Exception {
			//given
			PriceFilter priceFilter = new PriceFilter();
			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.sortOption(ItemSortOption.DISCOUNT_RATE_H)
				.build();

			//when
			Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
				"title",
				priceFilter,
				itemPage
			);

			//then
			assertThat(items.getContent())
				.hasSize(14)
				.isSortedAccordingTo(comparing(ItemQueryDto::getDiscountRate).reversed());

			assertThat(items.getTotalElements()).isEqualTo(15);
			assertThat(items.getTotalPages()).isEqualTo(2);
		}

		@DisplayName("조회된 아이템들을 가격순으로 오름차순 정렬할 수 있다.")
		@Test
		void 최저가_순() throws Exception {
			//given
			PriceFilter priceFilter = new PriceFilter();
			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.sortOption(ItemSortOption.PRICE_L)
				.build();

			//when
			Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
				"title",
				priceFilter,
				itemPage
			);

			//then
			assertThat(items.getContent())
				.hasSize(14)
				.isSortedAccordingTo(comparing(ItemQueryDto::getRealPrice));

			assertThat(items.getTotalElements()).isEqualTo(15);
			assertThat(items.getTotalPages()).isEqualTo(2);
		}

		@DisplayName("조회된 아이템들을 가격순으로 내림차순 정렬할 수 있다.")
		@Test
		void 최고가_순() throws Exception {
			//given
			PriceFilter priceFilter = new PriceFilter();
			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.sortOption(ItemSortOption.PRICE_H)
				.build();

			//when
			Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
				"title",
				priceFilter,
				itemPage
			);

			//then
			assertThat(items.getContent())
				.hasSize(14)
				.isSortedAccordingTo(comparing(ItemQueryDto::getRealPrice).reversed());

			assertThat(items.getTotalElements()).isEqualTo(15);
			assertThat(items.getTotalPages()).isEqualTo(2);
		}

		@DisplayName("조회된 아이템들을 리뷰가 많은 순으로 정렬할 수 있다.")
		@Test
		void 리뷰_순() throws Exception {
			//given
			PriceFilter priceFilter = new PriceFilter();
			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.sortOption(ItemSortOption.REVIEW_COUNT)
				.build();

			//when
			Page<ItemQueryDto> items = itemQueryRepository.findFilteredItems(
				"title",
				priceFilter,
				itemPage
			);

			//then
			assertThat(items.getContent())
				.hasSize(14)
				.isSortedAccordingTo(comparing(ItemQueryDto::getReviewSize).reversed());

			assertThat(items.getTotalElements()).isEqualTo(15);
			assertThat(items.getTotalPages()).isEqualTo(2);
			assertThat(items.getNumberOfElements()).isEqualTo(14);
		}
	}

	@Nested
	@DisplayName("가격 따른 필터링 테스트(1_000원 ~ 15_000원)")
	class PriceFilterTest {

		@DisplayName("가격이 1 만원에서 1만 5000원 필터링 조회 -> 높은 가격순")
		@Test
		void 가격_필터링1() throws Exception {
			//given
			PriceFilter priceFilter = PriceFilter.builder()
				.highPrice(15000)
				.lowPrice(10000)
				.build();

			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.sortOption(ItemSortOption.PRICE_H)
				.build();

			//when
			Page<ItemQueryDto> itemsByPrice =
				itemQueryRepository.findFilteredItems(
					null,
					priceFilter,
					itemPage
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
			PriceFilter priceFilter = PriceFilter.builder()
				.highPrice(1000)
				.lowPrice(14999)
				.build();

			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.sortOption(ItemSortOption.PRICE_L)
				.build();

			//when
			Page<ItemQueryDto> itemsByPrice =
				itemQueryRepository.findFilteredItems(
					null,
					priceFilter,
					itemPage
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
			PriceFilter priceFilter = PriceFilter.builder()
				.highPrice(1000)
				.lowPrice(1000)
				.build();

			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.build();

			//when
			Page<ItemQueryDto> itemsByPrice =
				itemQueryRepository.findFilteredItems(
					null,
					priceFilter,
					itemPage
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
			PriceFilter priceFilter = PriceFilter.builder()
				.highPrice(100110)
				.lowPrice(100110)
				.build();

			ItemPage itemPage = ItemPage.builder()
				.page(1)
				.size(14)
				.build();

			//when
			Page<ItemQueryDto> filteredItems = itemQueryRepository.findFilteredItems(
				null,
				priceFilter,
				itemPage
			);

			// then
			assertThat(filteredItems).isEmpty();
		}
	}

	private void persistItems() {

		var value = new AtomicInteger(0);
		var value1 = new AtomicReference<Double>(1D);

		List<Item> items = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", null)
			.setLazy("statistics.sales", () -> value.addAndGet(1))
			.setLazy("information.price.discountRate", () -> value1.getAndSet(value1.get() + 1D))
			.setLazy("information.price.realPrice", () -> value.intValue() * 1000)
			.setLazy("information.productName", () -> "title" + value)
			.set("reviewIds", new HashSet<>())
			.set("categories", new Categories(Set.of(BONE, EYE)))
			.set("itemCategory", Set.of(EYE, BONE))
			.sampleList(15);

		LongStream.range(1, 16).forEach(i ->
			{
				items.forEach(item -> item.getReviewIds().add(i));
			}
		);

		var value2 = new AtomicInteger(9);
		var items2 = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", null)
			.set("statistics.sales", 0)
			.set("information.price.discountRate", 0D)
			.set("information.price.realPrice", 1)
			.set("categories", null)
			.set("itemCategory", Set.of(ETC))
			.setLazy("information.productName", () -> "test" + value2.addAndGet(1))
			.sampleList(3);

		items.addAll(items2);
		items.forEach(em::persist);
	}
}