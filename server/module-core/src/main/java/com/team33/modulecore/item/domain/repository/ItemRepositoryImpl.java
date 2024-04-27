package com.team33.modulecore.item.domain.repository;

import static com.team33.modulecore.item.domain.QItem.item;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import com.team33.modulecore.item.dto.ItemSortOption;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> findByTitle(String title, ItemSearchRequest request) {

        List<Item> fetch = queryFactory.selectFrom(item)
            .where(
                titleContainsKeyword(title)
            )
            .limit(request.getSize())
            .offset(request.getOffset())
            .orderBy(getItemSort(request.getSortOption()))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(item.count())
            .from(item)
            .where(
                titleContainsKeyword(title)
            );

        return PageableExecutionUtils.getPage(
            fetch,
            PageRequest.of(request.getPage() - 1, request.getSize()),
            countQuery::fetchOne
        );
    }

    private BooleanExpression titleContainsKeyword(String title) {
        return StringUtils.isNullOrEmpty(title) ? null : item.title.contains(title);
    }

    private OrderSpecifier<Integer> getItemSort(ItemSortOption itemSortOption) {

        switch (itemSortOption) {
            case DISCOUNT_RATE_H:
                item.itemPrice.discountRate.desc();
            case DISCOUNT_RATE_L:
                item.itemPrice.discountRate.asc();
            case PRICE_H:
                item.itemPrice.realPrice.desc();
            case PRICE_L:
                item.itemPrice.realPrice.asc();
            default:
                return item.sales.desc();
        }
    }
}
