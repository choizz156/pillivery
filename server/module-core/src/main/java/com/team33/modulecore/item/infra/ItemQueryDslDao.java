package com.team33.modulecore.item.infra;

import static com.team33.modulecore.item.domain.entity.QItem.*;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.entity.QItem;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.query.ItemPage;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilter;
import com.team33.modulecore.item.dto.query.QItemQueryDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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
			return Page.empty();
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
		BooleanExpression discountRateEqNot0 = discountRateEqNot0();
		BooleanExpression productNameContainsKeyword = productNameContainsKeyword(keyword);
		BooleanExpression priceBetween = priceBetween(priceFilter);

		List<ItemQueryDto> fetch = selectItemQueryDto()
			.where(
				discountRateEqNot0,
				productNameContainsKeyword,
				priceBetween
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		if (isEmpty(fetch)) {
			return Page.empty();
		}

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.where(
				discountRateEqNot0,
				productNameContainsKeyword,
				priceBetween
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
			count::fetchOne
		);
	}

	private static BooleanExpression discountRateEqNot0() {
		return item.information.price.discountRate.eq(0D).not();
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
		BooleanExpression categoryNameEq = Expressions.enumPath(CategoryName.class, "itemCategory").eq(categoryName);

		List<ItemQueryDto> fetch = selectItemQueryDto()
			.innerJoin(item.itemCategory, Expressions.enumPath(CategoryName.class, "itemCategory"))
			.where(
				Expressions.enumPath(CategoryName.class, "itemCategory").eq(categoryName),
				productNameContainsKeyword,
				priceBetween
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		if (isEmpty(fetch)) {
			return Page.empty();
		}

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.innerJoin(item.itemCategory, Expressions.enumPath(CategoryName.class, "itemCategory"))
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

	private static boolean isEmpty(List<ItemQueryDto> fetch) {
		return fetch.isEmpty();
	}

	@Override
	public Page<ItemQueryDto> findByBrand(String keyword, ItemPage pageDto, PriceFilter priceFilter) {
		BooleanExpression keywordContainsEnterprise = keywordContainsEnterprise(keyword);
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
			return Page.empty();
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

	private BooleanExpression keywordContainsEnterprise(String keyword) {
		return StringUtils.isNullOrEmpty(keyword) ? null : item.information.enterprise.contains(keyword);
	}

	private BooleanExpression priceBetween(PriceFilter priceFilter) {
		if (priceFilter.isSumZero()) {
			return null;
		}

		if (priceFilter.isSamePriceEach()) {
			return item.information.price.realPrice.eq(priceFilter.getLowPrice());
		}

		priceFilter.checkReversedPrice();

		return item.information.price.realPrice.between(
			priceFilter.getLowPrice(),
			priceFilter.getHighPrice()
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

	private void checkEmptyList(Collection<?> fetch) {
		if (fetch.isEmpty()) {
			throw new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND);
		}
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
