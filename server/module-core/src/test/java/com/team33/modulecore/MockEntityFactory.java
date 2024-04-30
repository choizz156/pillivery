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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockEntityFactory {

    private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .plugin(new JavaxValidationPlugin())
        .build();

    private EntityManager entityManager;
    private final User user = getMockUser();

    private MockEntityFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static MockEntityFactory of(EntityManager entityManager) {
        return new MockEntityFactory(entityManager);
    }

    public static MockEntityFactory of() {
        return new MockEntityFactory();
    }

    public void persistEntity() {
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

    public void getMockOrders(User user, List<Item> mockItems) {
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

    public List<Item> getMockItems() {
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

    public Item getMockItem() {
        return FIXTURE_MONKEY.giveMeBuilder(Item.class)
            .set("id", null)
            .set("sales", 1)
            .set("itemPrice.discountRate", 3L)
            .set("itemPrice.realPrice", 3000)
            .set("title", "mockItem")
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("itemCategories", new HashSet<>())
            .sample();
    }

    public User getMockUser() {
        return FIXTURE_MONKEY.giveMeBuilder(User.class)
            .set("id", null)
            .set("wishList", null)
            .set("cart", null)
            .sample();
    }

    public List<OrderItem> getMockOrderItems(){
        return FIXTURE_MONKEY.giveMeBuilder(OrderItem.class).sampleList(3);
    }

    public Order getMockOrder() {
        return FIXTURE_MONKEY.giveMeOne(Order.class);
    }
}
