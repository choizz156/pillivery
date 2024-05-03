package com.team33.modulecore;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;
import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.CategoryName;
import com.team33.modulecore.item.domain.Brand;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.entity.ItemCategory;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.SubscriptionItemInfo;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockEntityFactory {

    private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .plugin(new JavaxValidationPlugin())
        .build();

    private EntityManager entityManager;

    private User user;

    private MockEntityFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static MockEntityFactory of(EntityManager entityManager) {
        return new MockEntityFactory(entityManager);
    }

    public static MockEntityFactory of() {
        return new MockEntityFactory();
    }

    public void persistItem() {
        persistMockItems();
    }

    public void persistOrder() {
        user = getMockUser();
        entityManager.persist(user);

        List<Item> mockItems = getMockItems();
        mockItems.forEach(entityManager::persist);

        persistMockOrder(user, mockItems);
    }

    public Item getMockItem() {
        return FIXTURE_MONKEY.giveMeBuilder(Item.class)
            .set("id", 1L)
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

    public List<OrderItem> getMockOrderItems() {
        return FIXTURE_MONKEY.giveMeBuilder(OrderItem.class)
            .set("item", getMockItem())
            .sampleList(3);
    }

    public OrderItem getMockOrderItem() {
        return FIXTURE_MONKEY.giveMeBuilder(OrderItem.class)
            .set("item", getMockItem())
            .sample();
    }

    public Order getMockOrder() {
        return FIXTURE_MONKEY.giveMeOne(Order.class);
    }

    public Order getMockOrderWithOrderItem() {
        return FIXTURE_MONKEY.giveMeBuilder(Order.class)
            .set("id", 1L)
            .set("orderItems", List.of(getMockOrderItem()))
            .set("user", null)
            .set("orderPrice", null)
            .sample();
    }

    public List<Order> getMockOrders() {
        OrderItem mockOrderItem = getMockOrderItem();
        List<Order> orders1 = FIXTURE_MONKEY.giveMeBuilder(Order.class)
            .set("orderItems", List.of(mockOrderItem))
            .set("user", null)
            .set("orderStatus", OrderStatus.COMPLETE)
            .set("orderPrice", null)
            .sampleList(7);

        List<Order> orders2 = FIXTURE_MONKEY.giveMeBuilder(Order.class)
            .set("orderItems", List.of(mockOrderItem))
            .set("user", null)
            .set("orderStatus", OrderStatus.SUBSCRIBE)
            .set("orderPrice", null)
            .sampleList(7);
        orders1.addAll(orders2);
        return orders1;
    }

    public User getMockUser() {
        return FIXTURE_MONKEY.giveMeBuilder(User.class)
            .set("id", null)
            .set("wishList", null)
            .set("cart", null)
            .sample();
    }

    public User getPersistedUser() {
        return user;
    }

    private void persistMockOrder(User user, List<Item> mockItems) {
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

    public void persistItemCategory(){
        List<Category> categories = List.of(Category.of(CategoryName.EYE), Category.of(CategoryName.BONE));
        categories.forEach(entityManager::persist);

        List<Item> mockItems = getMockItems();
        mockItems.forEach(entityManager::persist);

        Set<ItemCategory> collect = categories.stream().map(ItemCategory::of)
            .collect(Collectors.toSet());
        collect.forEach(entityManager::persist);

        mockItems.forEach(item -> collect.forEach(itemCategory -> itemCategory.addItem(item)));
        mockItems.forEach(entityManager::merge);
    }

    private List<Item> getMockItems() {
        return FIXTURE_MONKEY.giveMeBuilder(Item.class)
            .set("id", null)
            .set("itemPrice.discountRate", 0D)
            .set("nutritionFacts", new ArrayList<>())
            .set("reviews", null)
            .set("brand", Brand.MYNI)
            .set("wishList", null)
            .set("itemCategories", new HashSet<>())
            .sampleList(5);
    }

    private void persistMockItems()  {

        var value = new AtomicInteger(0);
        var value1 = new AtomicReference<Double>(1D);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            executorService.submit(() -> {
                List<Item> itemList = IntStream.range(1, 100000).mapToObj(
                    i -> FIXTURE_MONKEY.giveMeBuilder(Item.class)
                        .set("id", null)
                        .setLazy("sales", () -> value.addAndGet(1))
                        .setLazy("itemPrice.discountRate",
                            () -> value1.getAndSet(value1.get() + 1D))
                        .setLazy("itemPrice.realPrice", () -> value.intValue() * 1000)
                        .setLazy("title", () -> "title" + value)
                        .set("nutritionFacts", new ArrayList<>())
                        .set("reviews", null)
                        .set("brand", Brand.MYNI)
                        .set("wishList", null)
                        .set("itemCategories", new HashSet<>())
                        .sample()
                ).collect(Collectors.toList());
                itemList.forEach(entityManager::persist);
            });


//        List<Item> collect = FIXTURE_MONKEY.giveMeBuilder(Item.class)
//            .set("id", null)
//            .setLazy("sales", () -> value.addAndGet(1))
//            .setLazy("itemPrice.discountRate", () -> value1.getAndSet(value1.get() + 1D))
//            .setLazy("itemPrice.realPrice", () -> value.intValue() * 1000)
//            .setLazy("title", () -> "title" + value)
//            .set("nutritionFacts", new ArrayList<>())
//            .set("reviews", null)
//            .set("brand", Brand.MYNI)
//            .set("wishList", null)
//            .set("itemCategories", new HashSet<>())
//            .sampleList(100)

//            .sampleList(1000).parallelStream().forEach(entityManager::persist);

//        var value2 = new AtomicInteger(9);
//        var items2 = FIXTURE_MONKEY.giveMeBuilder(Item.class)
//            .set("id", null)
//            .set("sales", 0)
//            .set("itemPrice.discountRate", 0)
//            .set("itemPrice.realPrice", 1)
//            .setLazy("title", () -> "test" + value2.addAndGet(1))
//            .set("nutritionFacts", new ArrayList<>())
//            .set("reviews", null)
//            .set("brand", Brand.MYNI)
//            .set("wishList", null)
//            .set("itemCategories", new HashSet<>())
//            .sampleList(3);
//
//        items.addAll(items2);
//        items.forEach(entityManager::persist);
    }
}
