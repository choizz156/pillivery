package com.team33.modulecore.item.infra;


import static com.team33.modulecore.item.domain.entity.QItem.item;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ItemQueryRepositoryImpl implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> findByTitle(String title, ItemSearchRequest request) {

        List<Item> fetch = queryFactory.selectFrom(item).where(titleContainsKeyword(title))
            .limit(request.getSize()).offset(request.getOffset())
            .orderBy(getItemSort(request.getSortOption())).fetch();

        JPAQuery<Long> countQuery = queryFactory.select(item.count()).from(item)
            .where(titleContainsKeyword(title));

        return PageableExecutionUtils.getPage(fetch,
            PageRequest.of(request.getPage() - 1, request.getSize()), countQuery::fetchOne);
    }

    @Override
    public List<Item> findItemsWithSalesTop9() {
        return queryFactory.selectFrom(item).orderBy(item.sales.desc()).limit(9).fetch();
    }

    @Override
    public List<Item> findItemsWithDiscountRateTop9() {
        return queryFactory.selectFrom(item)
            .limit(9)
            .orderBy(item.itemPrice.discountRate.desc())
            .fetch();
    }

    private BooleanExpression titleContainsKeyword(String title) {
        return StringUtils.isNullOrEmpty(title) ? null : item.title.contains(title);
    }

    private OrderSpecifier<? extends Number> getItemSort(ItemSortOption itemSortOption) {

        switch (itemSortOption) {
            case DISCOUNT_RATE_H:
                return item.itemPrice.discountRate.desc();
            case DISCOUNT_RATE_L:
                return item.itemPrice.discountRate.asc();
            case PRICE_H:
                return item.itemPrice.realPrice.desc();
            case PRICE_L:
                return item.itemPrice.realPrice.asc();
            default:
                return item.sales.desc();
        }
    }
}
