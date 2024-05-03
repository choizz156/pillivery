package com.team33.modulecore.item.infra;


import static com.team33.modulecore.category.domain.QCategory.category;
import static com.team33.modulecore.item.domain.entity.QItem.item;
import static com.team33.modulecore.item.domain.entity.QItemCategory.itemCategory;
import static com.team33.modulecore.item.domain.entity.QNutritionFact.nutritionFact;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.entity.NutritionFact;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.query.ItemPageDto;
import com.team33.modulecore.item.dto.PriceFilterDto;
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
        List<ItemQueryDto> fetch = selectItemQueryDtoFromItem()
            .orderBy(item.sales.desc())
            .limit(9)
            .fetch();

        checkEmptyList(fetch);

        return fetch;
    }

    @Override
    public List<ItemQueryDto> findItemsWithDiscountRateTop9() {
        List<ItemQueryDto> fetch = selectItemQueryDtoFromItem()
            .limit(9)
            .orderBy(item.itemPrice.discountRate.desc())
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
        List<ItemQueryDto> fetch = selectItemQueryDtoFromItem()
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
        List<ItemQueryDto> fetch = queryFactory
            .select(
                Projections.fields(ItemQueryDto.class,
                        item.id.as("itemId"),
                        item.thumbnail,
                        item.title,
                        item.content,
                        item.capacity,
                        item.itemPrice.realPrice,
                        item.itemPrice.discountRate,
                        item.itemPrice.discountPrice,
                        item.sales,
                        item.starAvg,
                        item.reviews.size().as("reviewSize")
                    ))
                    .from(item)
                    .where(
                        item.itemPrice.discountRate.eq(0D).not()
                    )
                    .limit(pageDto.getSize())
                    .offset(pageDto.getOffset())
                    .orderBy(getItemSort(pageDto.getSortOption()))
                    .fetch();

        List<CategoryName> fetch1 = queryFactory
            .select(itemCategory.category.categoryName)
            .from(itemCategory)
            .innerJoin(itemCategory.item, item)
            .innerJoin(itemCategory.category, category)
            .where(itemCategory.item.id.eq(item.id)).fetch();


        List<NutritionFact> fetch2 = queryFactory
            .selectFrom(nutritionFact)
            .where(nutritionFact.item.id.eq(item.id))
            .fetch();

        checkEmptyList(fetch);

        JPAQuery<Long> count = queryFactory
            .select(item.count())
            .from(item)
            .where(item.itemPrice.discountRate.eq(0D).not());

        return PageableExecutionUtils.getPage(
            fetch,
            PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
            count::fetchOne
        );
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
            return item.itemPrice.realPrice.eq(priceFilter.getLowPrice());
        }

        priceFilter.checkReversedPrice();

        return item.itemPrice.realPrice.between(
            priceFilter.getLowPrice(),
            priceFilter.getHighPrice()
        );
    }

    private BooleanExpression titleContainsKeyword(String title) {
        return StringUtils.isNullOrEmpty(title) ? null : item.title.contains(title);
    }

    private OrderSpecifier<? extends Number> getItemSort(ItemSortOption itemSortOption) {
        return itemSortOption.getSort();
    }

    private void checkEmptyList(Collection<?> fetch) {
        if (fetch.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND);
        }
    }

    private JPAQuery<ItemQueryDto> selectItemQueryDtoFromItem() {
        return queryFactory
            .select(
                Projections.fields(ItemQueryDto.class,
                    item.id.as("itemId"),
                    item.thumbnail,
                    item.title,
                    item.content,
                    item.capacity,
                    item.itemPrice.realPrice,
                    item.itemPrice.discountRate,
                    item.itemPrice.discountPrice,
                    item.sales,
                    item.starAvg,
                    item.reviews.size().as("reviewSize"),
                    ExpressionUtils.as(
                        JPAExpressions
                            .select(itemCategory.category.categoryName)
                            .from(itemCategory)
                            .innerJoin(itemCategory.item, item)
                            .innerJoin(itemCategory.category, category)
                            .where(itemCategory.item.id.eq(item.id))
                        , "categoryName"
                    ),
                    ExpressionUtils.as(
                        JPAExpressions
                            .selectFrom(nutritionFact)
                            .where(nutritionFact.item.id.eq(item.id))
                        , "nutritionFact"
                    )
                ))
            .from(item);
    }

    @Override
    public Page<Item> findItemsOnSale2(ItemPageDto pageDto) {
        List<Item> fetch = queryFactory.
            select(item)
            .from(item)
            .where(
                item.itemPrice.discountRate.eq(0D).not()
            )
            .limit(pageDto.getSize())
            .offset(pageDto.getOffset())
            .orderBy(getItemSort(pageDto.getSortOption()))
            .fetch();

        checkEmptyList(fetch);

        JPAQuery<Long> count = queryFactory
            .select(item.count())
            .from(item)
            .where(item.itemPrice.discountRate.eq(0D).not());

        return PageableExecutionUtils.getPage(
            fetch,
            PageRequest.of(pageDto.getPage() - 1, pageDto.getSize()),
            count::fetchOne
        );

    }
}
