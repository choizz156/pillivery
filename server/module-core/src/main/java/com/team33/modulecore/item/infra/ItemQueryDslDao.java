package com.team33.modulecore.item.infra;

import static com.team33.modulecore.item.domain.entity.QItem.*;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import com.team33.modulecore.item.dto.query.ItemPageDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import com.team33.modulecore.item.dto.query.PriceFilterDto;
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
			.fetchOne();

		if (item == null) {
			throw new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND);
		}

		return item;
	}

	@Override
	public List<ItemQueryDto> findItemsWithSalesTop9() {
		List<ItemQueryDto> fetch = selectItemQueryDto()
			.from(item)
			.orderBy(item.statistics.sales.desc())
			.limit(9)
			.fetch();

		checkEmptyList(fetch);

		return fetch;
	}

	@Override
	public List<ItemQueryDto> findItemsWithDiscountRateTop9() {
		List<ItemQueryDto> fetch = selectItemQueryDto()
			.from(item)
			.limit(9)
			.orderBy(item.information.price.discountRate.desc())
			.fetch();

		checkEmptyList(fetch);

		return fetch;
	}

	@Override
	public Page<ItemQueryDto> findFilteredItems(
		String keyword,
		PriceFilterDto priceFilter,
		ItemPageDto pageDto
	) {
		List<ItemQueryDto> fetch = selectItemQueryDto()
			.from(item)
			.where(
				titleContainsKeyword(keyword),
				priceBetween(priceFilter)
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		checkEmptyList(fetch);

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.where(priceBetween(priceFilter));

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
			count::fetchOne
		);
	}

	@Override
	public Page<ItemQueryDto> findItemsOnSale(String keyword, PriceFilterDto priceFilter, ItemPageDto pageDto) {
		List<ItemQueryDto> fetch = selectItemQueryDto()
			.where(
				discountRateEqNot0(),
				titleContainsKeyword(keyword),
				priceBetween(priceFilter)
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		checkEmptyList(fetch);

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.where(discountRateEqNot0());

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
		PriceFilterDto priceFilter,
		ItemPageDto pageDto
	) {
		List<ItemQueryDto> fetch = selectItemQueryDto()
			.where(
				item.includedCategories.contains(categoryName),
				titleContainsKeyword(keyword),
				priceBetween(priceFilter)
			)
			.limit(pageDto.getSize())
			.offset(pageDto.getOffset())
			.orderBy(getItemSort(pageDto.getSortOption()))
			.fetch();

		checkEmptyList(fetch);

		JPAQuery<Long> count = queryFactory
			.select(item.count())
			.from(item)
			.where(item.includedCategories.contains(categoryName));

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
			count::fetchOne
		);
	}

	private BooleanExpression priceBetween(PriceFilterDto priceFilter) {
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

	private BooleanExpression titleContainsKeyword(String keyword) {
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
					item.statistics.reviewCount
				))
			.from(item);
	}
}
