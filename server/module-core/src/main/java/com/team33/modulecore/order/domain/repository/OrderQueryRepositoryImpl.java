package com.team33.modulecore.order.domain.repository;

import static com.team33.modulecore.order.domain.OrderStatus.REQUEST;
import static com.team33.modulecore.order.domain.OrderStatus.SUBSCRIBE;
import static com.team33.modulecore.order.domain.QOrder.order;
import static com.team33.modulecore.order.domain.QOrderItem.orderItem;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.user.domain.User;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

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
            .select(orderItem).
            from(orderItem)
            .innerJoin(orderItem.order, order)
            .where(
                orderUserAndOrderItemUserEq(),
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

    private  BooleanExpression orderUserAndOrderItemUserEq() {
        return orderItem.order.user.id.eq(order.user.id);
    }

    private BooleanExpression subscriptionOrderStatusEq(OrderStatus orderStatus) {
        return orderStatus == SUBSCRIBE
            ? order.orderStatus.eq(orderStatus)
            : null;
    }

    private BooleanExpression userEq(User user) {
        return user == null ? null : order.user.eq(user);
    }

    private BooleanExpression notOrderStatusRequest(OrderStatus orderStatus) {
        return orderStatus == REQUEST
            ? order.orderStatus.eq(orderStatus).not()
            : null;

    }

    private OrderSpecifier<Long> getOrderSort(Direction pageRequest) {
        return pageRequest == Direction.DESC ? order.id.desc() : order.id.asc();
    }

    private OrderSpecifier<Long> getSubscriptionOrderSort(Direction pageRequest) {
        return pageRequest == Direction.DESC ? orderItem.id.desc() : orderItem.id.asc();
    }
}