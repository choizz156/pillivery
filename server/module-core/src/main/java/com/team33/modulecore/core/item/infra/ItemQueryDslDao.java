package com.team33.modulecore.core.item.infra;

import static com.team33.modulecore.core.item.domain.entity.QItem.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.ItemSortOption;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.entity.QItem;
import com.team33.modulecore.core.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.core.item.dto.query.ItemPage;
import com.team33.modulecore.core.item.dto.query.ItemQueryDto;
import com.team33.modulecore.core.item.dto.query.PriceFilter;
import com.team33.modulecore.core.item.dto.query.QItemQueryDto;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ItemQueryDslDao implements ItemQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Item findById(long id) {

		Item item = queryFactory
			.selectFrom(QItem.item)
			.where(QItem.item.id.eq(id))
			.fetchFirst();

		if (item == null) {
			throw new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND);
		}

		return item;
	}

	@Override
	public List<ItemQueryDto> findItemsWithSalesMain() {

		List<ItemQueryDto> fetch = selectItemQueryDto()
			.from(item)
			.orderBy(item.statistics.sales.desc())
			.limit(9)
			.fetch();

		if (isEmpty(fetch)) {
			return List.of();
		}

		return fetch;
	}

	@Override
	public List<ItemQueryDto> findItemsWithDiscountRateMain() {

		List<ItemQueryDto> fetch = selectItemQueryDto()
			.from(item)
			.limit(9)
			.orderBy(item.information.price.discountRate.desc())
			.fetch();

		if (isEmpty(fetch)) {
			return List.of();
		}

		return fetch;
	}

	@Override
	public Page<ItemQueryDto> findFilteredItems(
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {

		BooleanExpression productNameContainsKeyword = productNameContainsKeyword(keyword);
		BooleanExpression priceBetween = priceBetween(priceFilter);

		List<ItemQueryDto> fetch = selectItemQueryDto()
			.from(item)
			.where(
				productNameContainsKeyword,
				priceBetween
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		if (isEmpty(fetch)) {
			return Page.empty(Pageable.ofSize(1));
		}

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.where(
				productNameContainsKeyword,
				priceBetween
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
			count::fetchOne
		);
	}

	@Override
	public Page<ItemQueryDto> findItemsOnSale(String keyword, PriceFilter priceFilter, ItemPage pageDto) {

		BooleanExpression discountRateGT0 = discountRateGt();
		BooleanExpression productNameContainsKeyword = productNameContainsKeyword(keyword);
		BooleanExpression priceBetween = priceBetween(priceFilter);

		List<ItemQueryDto> fetch = selectItemQueryDto()
			.where(
				discountRateGT0,
				priceBetween,
				productNameContainsKeyword
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		if (isEmpty(fetch)) {
			return Page.empty(Pageable.ofSize(1));
		}

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.where(
				discountRateGT0,
				productNameContainsKeyword,
				priceBetween
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
			count::fetchOne
		);
	}

	@Override
	public Page<ItemQueryDto> findItemsByCategory(
		CategoryName categoryName,
		String keyword,
		PriceFilter priceFilter,
		ItemPage pageDto
	) {

		BooleanExpression productNameContainsKeyword = productNameContainsKeyword(keyword);
		BooleanExpression priceBetween = priceBetween(priceFilter);

		String itemCategory = "itemCategory";
		BooleanExpression categoryNameEq = Expressions.enumPath(CategoryName.class, itemCategory).eq(categoryName);

		List<ItemQueryDto> fetch = selectItemQueryDto()
			.innerJoin(item.itemCategory, Expressions.enumPath(CategoryName.class, itemCategory))
			.where(
				categoryNameEq,
				productNameContainsKeyword,
				priceBetween
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		if (isEmpty(fetch)) {
			return Page.empty(Pageable.ofSize(1));
		}

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.innerJoin(item.itemCategory, Expressions.enumPath(CategoryName.class, itemCategory))
			.where(
				categoryNameEq,
				productNameContainsKeyword,
				priceBetween
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
			count::fetchOne
		);
	}

	@Override
	public Page<ItemQueryDto> findByBrand(String brand, ItemPage pageDto, PriceFilter priceFilter) {

		BooleanExpression keywordContainsEnterprise = keywordContainsEnterprise(brand);
		BooleanExpression priceBetween = priceBetween(priceFilter);

		List<ItemQueryDto> fetch = selectItemQueryDto().
			where(
				keywordContainsEnterprise,
				priceBetween
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		if (isEmpty(fetch)) {
			return Page.empty(Pageable.ofSize(1));
		}

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.where(
				keywordContainsEnterprise,
				priceBetween
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
			count::fetchOne
		);
	}

	private static BooleanExpression discountRateGt() {

		return item.information.price.discountRate.gt(0D);
	}

	private boolean isEmpty(List<ItemQueryDto> fetch) {

		return fetch.isEmpty();
	}

	private BooleanExpression keywordContainsEnterprise(String keyword) {

		return StringUtils.isNullOrEmpty(keyword) ? null : item.information.enterprise.contains(keyword);
	}

	private BooleanExpression priceBetween(PriceFilter priceFilter) {

		if (priceFilter.isSumZero()) {
			return null;
		}

		if (priceFilter.isSamePriceEach()) {
			return item.information.price.realPrice.eq(priceFilter.getLow());
		}

		priceFilter.checkReversedPrice();

		return item.information.price.realPrice.between(
			priceFilter.getLow(),
			priceFilter.getHigh()
		);
	}

	private BooleanExpression productNameContainsKeyword(String keyword) {

		return StringUtils.isNullOrEmpty(keyword)
			? null
			: item.information.productName.contains(keyword);
	}

	private OrderSpecifier<? extends Number> getItemSort(ItemSortOption itemSortOption) {

		return itemSortOption.getSort();
	}

	private JPAQuery<ItemQueryDto> selectItemQueryDto() {

		return queryFactory
			.select(
				new QItemQueryDto(
					item.id.as("itemId"),
					item.information.image.thumbnail,
					item.information.productName,
					item.information.enterprise,
					item.information.mainFunction,
					item.information.baseStandard,
					item.information.price.realPrice,
					item.information.price.discountRate,
					item.information.price.discountPrice,
					item.statistics.sales,
					item.statistics.starAvg,
					item.statistics.reviewCount,
					item.categories
				)
			)
			.from(item);
	}
}
