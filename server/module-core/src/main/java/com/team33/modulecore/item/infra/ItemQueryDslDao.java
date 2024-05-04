package com.team33.modulecore.item.infra;


import static com.team33.modulecore.category.domain.QCategory.category;
import static com.team33.modulecore.category.domain.QItemCategory.itemCategory;
import static com.team33.modulecore.item.domain.entity.QItem.item;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemPageDto;
import com.team33.modulecore.item.dto.query.ItemQueryDto;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ItemQueryDslDao implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

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
    public Page<ItemQueryDto> findItemsOnSale(ItemPageDto pageDto) {
        List<ItemQueryDto> fetch =selectItemQueryDto()
            .from(item)
            .where(
                discountPriceEqNot0()
            )
            .limit(pageDto.getSize())
            .offset(pageDto.getOffset())
            .orderBy(getItemSort(pageDto.getSortOption()))
            .fetch();

        List<CategoryName> categoryNames = queryFactory
            .select(itemCategory.category.categoryName)
            .from(itemCategory)
            .innerJoin(itemCategory.item, item)
            .innerJoin(itemCategory.category, category)
            .where(itemCategory.item.id.eq(item.id))
            .fetch();

        checkEmptyList(fetch);

        JPAQuery<Long> count = queryFactory
            .select(item.count())
            .from(item)
            .where(discountPriceEqNot0());

        return PageableExecutionUtils.getPage(
            fetch,
            PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
            count::fetchOne
        );
    }

    private static BooleanExpression discountPriceEqNot0() {
        return item.information.price.discountRate.eq(0D).not();
    }

    @Override
    public Page<ItemQueryDto> findItemsByCategory(CategoryName categoryName, ItemPageDto pageDto) {
//        List<ItemQueryDto> fetch = queryFactory
//            .select(
//                Projections.fields(ItemQueryDto.class,
//                    item.id.as("itemId"),
//                    item.thumbnail,
//                    item.title,
//                    item.content,
//                    item.capacity,
//                    item.itemPrice.realPrice,
//                    item.itemPrice.discountRate,
//                    item.itemPrice.discountPrice,
//                    item.sales,
//                    item.starAvg,
//                    item.reviews.size().as("reviewSize"),
//                    ExpressionUtils.as(
//                        JPAExpressions
//                            .select(itemCategory.category.categoryName)
//                            .from(itemCategory)
//                            .where(itemCategory.item.id.eq(item.id))
//                        , "categoryName"
//                    ),
//                    ExpressionUtils.as(
//                        JPAExpressions
//                            .selectFrom(nutritionFact)
//                            .where(nutritionFact.item.id.eq(item.id))
//                        , "nutritionFact"
//                    )
//                ))
//            .from(itemCategory)
//            .innerJoin(itemCategory.item, item)
//            .innerJoin(itemCategory.category, category)
//            .where(
//                itemCategory.category.categoryName.eq(categoryName)
//            )
//            .limit(pageDto.getSize())
//            .offset(pageDto.getOffset())
//            .orderBy(getItemSort(pageDto.getSortOption()))
//            .fetch();
//
//        checkEmptyList(fetch);
//
//        JPAQuery<Long> count = queryFactory
//            .select(item.count())
//            .from(itemCategory)
//            .innerJoin(itemCategory.item, item)
//            .innerJoin(itemCategory.category, category)
//            .where(
//                itemCategory.category.categoryName.eq(categoryName)
//            );

//        return PageableExecutionUtils.getPage(
//            fetch,
//            PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
//            count::fetchOne
//        );

        return null;
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

    private BooleanExpression titleContainsKeyword(String productName) {
        return StringUtils.isNullOrEmpty(productName)
            ? null
            : item.information.productName.contains(productName);
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
                Projections.fields(ItemQueryDto.class,
                    item.id.as("itemId"),
                    item.information.image.thumbnail,
                    item.information.image.descriptionImage,
                    item.information.productName,
                    item.information.mainFunction,
                    item.information.enterprise,
                    item.information.baseStandard,
                    item.information.price.realPrice,
                    item.information.price.discountRate,
                    item.information.price.discountPrice,
                    item.statistics.sales,
                    item.statistics.starAvg
                ));
    }
}
