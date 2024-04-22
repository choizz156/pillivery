package com.team33.modulecore.order.repository;

import static com.team33.modulecore.order.domain.QOrder.order;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.order.domain.Order;
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
                notOrderStatusRequest(orderFindCondition.getOrderStatus()),
                isSubscription(orderFindCondition.isSubscription())
            )
            .limit(pageRequest.getSize())
            .offset(pageRequest.getOffset())
            .orderBy(getSort(pageRequest))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(order.count())
            .from(order)
            .where(
                userEq(orderFindCondition.getUser()),
                notOrderStatusRequest(orderFindCondition.getOrderStatus()),
                isSubscription(orderFindCondition.isSubscription())
            );

        return PageableExecutionUtils.getPage(
            contents,
            PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), Sort.by(pageRequest.getSort(), "id")),
            countQuery::fetchOne
        );
    }

    private BooleanExpression userEq(User user) {
        return user == null ? null : order.user.eq(user);
    }

    private BooleanExpression notOrderStatusRequest(OrderStatus orderStatus) {
        return StringUtils.isNullOrEmpty(orderStatus.name())
            ? null
            : order.orderStatus.eq(orderStatus).not();
    }

    private BooleanExpression isSubscription(boolean subscription) {
        return order.subscription.eq(subscription);
    }

    private OrderSpecifier<Long> getSort(OrderPageRequest pageRequest) {
       return pageRequest.getSort() == Direction.DESC ? order.id.desc() : order.id.asc();
    }
}
