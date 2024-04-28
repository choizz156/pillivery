package com.team33.modulecore.item.domain.mock;

import static com.team33.modulecore.item.domain.entity.QItem.item;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.ItemSortOption;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemSearchRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;


public class TestItemQueryRepository implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final EntityManager entityManager;

    private final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    public TestItemQueryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(this.entityManager);
        getMockItem();
    }

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
        return queryFactory.selectFrom(item).limit(9).orderBy(item.itemPrice.discountRate.desc())
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

    private void getMockItem() {
        var value = new AtomicInteger(1);
        var value1 = new AtomicReference<Double>(1D);
        var items = FIXTURE_MONKEY.giveMeBuilder(Item.class)
            .set("id", null)
            .setLazy("sales", () -> value.addAndGet(1))
            .setLazy("itemPrice.discountRate", () -> value1.getAndSet(value1.get() + 1D))
            .setLazy("itemPrice.realPrice", value::intValue)
            .setLazy("title", () -> "title" + value)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("itemCategories", new HashSet<>())
            .sampleList(15);

        var value2 = new AtomicInteger(9);
        var items2 = FIXTURE_MONKEY.giveMeBuilder(Item.class)
            .set("id", null)
            .set("sales", 0)
            .set("itemPrice.discountRate", 0)
            .set("itemPrice.realPrice", 1)
            .setLazy("title", () -> "test" + value2.addAndGet(1))
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("itemCategories", new HashSet<>())
            .sampleList(3);

        items.addAll(items2);
        items.forEach(this.entityManager::persist);
    }
}
