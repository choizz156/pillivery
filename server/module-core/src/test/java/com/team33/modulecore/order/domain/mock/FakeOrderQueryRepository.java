package com.team33.modulecore.order.domain.mock;

import static com.team33.modulecore.order.domain.OrderStatus.COMPLETE;
import static com.team33.modulecore.order.domain.OrderStatus.REQUEST;
import static com.team33.modulecore.order.domain.OrderStatus.SUBSCRIBE;
import static com.team33.modulecore.order.domain.QOrder.order;
import static com.team33.modulecore.order.domain.QOrderItem.orderItem;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.user.domain.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;

public class FakeOrderQueryRepository implements OrderQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .plugin(new JavaxValidationPlugin())
        .build();

    public FakeOrderQueryRepository(User user, EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.entityManager = entityManager;
        entityManager.persist(user);
        User user1 = entityManager.find(User.class, user.getId());

        List<Order> mockOrder1 = getMockOrder(user1);

        mockOrder1.forEach(entityManager::persist);


    }

    @Override
    public Page<Order> searchOrders(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    ) {
        List<Order> contents = queryFactory
            .selectFrom(order)
            .where(
                userEq(orderFindCondition.getUser()),
                notOrderStatusRequest(orderFindCondition.getOrderStatus())
            )
            .limit(pageRequest.getSize())
            .offset(pageRequest.getOffset())
            .orderBy(getOrderSort(pageRequest.getSort()))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(order.count())
            .from(order)
            .where(
                userEq(orderFindCondition.getUser()),
                notOrderStatusRequest(orderFindCondition.getOrderStatus())
            );

        return PageableExecutionUtils.getPage(
            contents,
            PageRequest.of(
                pageRequest.getPage() - 1,
                pageRequest.getSize(),
                Sort.by(pageRequest.getSort(), "id")
            ),
            countQuery::fetchOne
        );
    }

    @Override
    public List<OrderItem> findSubscriptionOrderItem(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    ) {
        List<OrderItem> fetch = queryFactory
            .select(orderItem)
            .from(orderItem)
            .join(orderItem.order, order)
            .where(
                userEq(orderFindCondition.getUser()),
                subscriptionOrderStatusEq(orderFindCondition.getOrderStatus())
            )
            .limit(pageRequest.getSize())
            .offset(pageRequest.getOffset())
            .orderBy(getSubscriptionOrderSort(pageRequest.getSort()))
            .fetch();
        return Collections.unmodifiableList(fetch);
    }

    @Override
    public Order findById(Long id) {
        Order fetch = queryFactory.selectFrom(order).where(order.id.eq(id)).fetchOne();
        if (fetch == null) {
            throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
        }
        return fetch;
    }

    private Predicate subscriptionOrderStatusEq(OrderStatus orderStatus) {
        return orderStatus == SUBSCRIBE
            ? order.orderStatus.eq(orderStatus)
            : null;
    }

    private BooleanExpression userEq(User user) {
        return user == null ? null : order.user.eq(user);
    }

    private BooleanExpression notOrderStatusRequest(OrderStatus orderStatus) {
        return orderStatus == REQUEST
            ? null
            : order.orderStatus.eq(orderStatus).not();
    }

    private OrderSpecifier<Long> getOrderSort(Direction pageRequest) {
        return pageRequest == Direction.DESC ? order.id.desc() : order.id.asc();
    }

    private OrderSpecifier<Long> getSubscriptionOrderSort(Direction pageRequest) {
        return pageRequest == Direction.DESC ? orderItem.id.desc() : orderItem.id.asc();
    }


    private List<Order> getMockOrder(User user) {
        List<Order> orders = FIXTURE_MONKEY.giveMeBuilder(Order.class)
            .set("id", null)
            .setLazy("user", () -> user)
            .set("orderStatus", SUBSCRIBE)
            .set("orderItems", new ArrayList<>())
            .set("isSubscription", true)
            .sampleList(3);
        List<Order> ordersNoSubs = FIXTURE_MONKEY.giveMeBuilder(Order.class)
            .set("id", null)
            .set("user", user)
            .set("orderStatus", COMPLETE)
            .set("orderItems", new ArrayList<>())
            .set("isSubscription", false)
            .sampleList(3);

        orders.addAll(ordersNoSubs);
        return orders;
    }

}
