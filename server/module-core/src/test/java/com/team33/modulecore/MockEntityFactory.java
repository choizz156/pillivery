package com.team33.modulecore;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.SubscriptionItemInfo;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.persistence.EntityManager;

public class MockEntityFactory {

    private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .plugin(new JavaxValidationPlugin())
        .build();

    private final EntityManager entityManager;

    private MockEntityFactory(EntityManager entityManager, User user) {
        this.entityManager = entityManager;
        persistEntity(user);
    }

    public static void start(EntityManager entityManager, User user) {
         new MockEntityFactory(entityManager, user);
    }

    private void persistEntity(User user) {
        entityManager.persist(user);

        List<Item> mockItems = getMockItems();
        mockItems.forEach(entityManager::persist);

        getMockOrders(user, mockItems);
        List<OrderItem> resultList = entityManager.createQuery(
                "select oi from OrderItem oi inner join orders o on oi.order.id = o.id where oi.order.user.id = o.user.id and o.orderStatus = : orderStatus",
                OrderItem.class)
            .setParameter("orderStatus", OrderStatus.SUBSCRIBE)
            .getResultList();
      }

    private void getMockOrders(User user, List<Item> mockItems) {
        mockItems.stream()
            .takeWhile(item -> item.getId() <= 8)
            .map(item -> OrderItem.create(
                item,
                SubscriptionItemInfo.of(false, 60),
                1
            ))
            .map(orderItem -> Order.create(List.of(orderItem), false, user))
            .forEach(order -> {
                order.changeOrderStatus(OrderStatus.COMPLETE);
                entityManager.persist(order);
            });

        mockItems.stream()
            .dropWhile(item -> item.getId() <= 8)
            .map(item -> OrderItem.create(
                item,
                SubscriptionItemInfo.of(true, 60),
                1
            ))
            .map(orderItem -> Order.create(List.of(orderItem), true, user))
            .forEach(order -> {
                order.changeOrderStatus(OrderStatus.SUBSCRIBE);
                entityManager.persist(order);
            });
    }

    private List<Item> getMockItems() {
        var sales = new AtomicInteger(1);
        var discountRate = new AtomicReference<Double>(1D);
        return FIXTURE_MONKEY.giveMeBuilder(Item.class)
            .set("id", null)
            .setLazy("sales", () -> sales.addAndGet(1))
            .setLazy("itemPrice.discountRate",
                () -> discountRate.getAndSet(discountRate.get() + 1D))
            .setLazy("itemPrice.realPrice", sales::intValue)
            .setLazy("title", () -> "title" + sales)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("itemCategories", new HashSet<>())
            .sampleList(15);
    }
}
