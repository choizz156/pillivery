package com.team33.modulecore.order.domain.repository;

import static com.team33.modulecore.order.domain.OrderStatus.REQUEST;
import static com.team33.modulecore.order.domain.OrderStatus.SUBSCRIBE;
import static com.team33.modulecore.order.domain.QOrder.order;
import static com.team33.modulecore.order.domain.QOrderItem.orderItem;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;


@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

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
            .orderBy(getOrderSort(pageRequest))
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
    public List<OrderItem> findSubscriptionOrder(
        OrderPageRequest pageRequest,
        OrderFindCondition orderFindCondition
    ) {
        return queryFactory
            .select(orderItem)
            .from(orderItem)
            .join(orderItem.order, order)
            .where(
                userEq(orderFindCondition.getUser()),
                subscriptionOrderStatusEq(orderFindCondition.getOrderStatus())
            )
            .limit(pageRequest.getSize())
            .offset(pageRequest.getOffset())
            .orderBy(getSubscirptionOrderSort(pageRequest))
            .fetch();
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


    private OrderSpecifier<Long> getOrderSort(OrderPageRequest pageRequest) {
        return pageRequest.getSort() == Direction.DESC ? order.id.desc() : order.id.asc();
    }

    private OrderSpecifier<Long> getSubscirptionOrderSort(OrderPageRequest pageRequest) {
        return pageRequest.getSort() == Direction.DESC ? orderItem.id.desc() : orderItem.id.asc();
    }
}
