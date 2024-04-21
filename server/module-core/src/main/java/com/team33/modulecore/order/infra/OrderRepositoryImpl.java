package com.team33.modulecore.order.infra;

import static com.team33.modulecore.order.domain.QOrder.order;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.OrderRepositoryCustom;
import com.team33.modulecore.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;


@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findPaidOrders(
        OrderPageRequest pageRequest,
        User user,
        OrderStatus orderStatus,
        boolean subscription
    ) {

        List<Order> contents = queryFactory.selectFrom(order).
            where(
                userEq(user),
                notOrderStatusRequest(orderStatus),
                isSubscription(subscription)
            )
            .limit(pageRequest.getSize())
            .offset(pageRequest.getOffset())
            .orderBy(getSort(pageRequest))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(order.count()).from(order).
            where(
                userEq(user),
                notOrderStatusRequest(orderStatus),
                isSubscription(subscription)
            );

        return PageableExecutionUtils.getPage(
            contents,
            pageRequest.getPageRequest(),
            countQuery::fetchOne
        );
    }

    private BooleanExpression userEq(User user) {
        return user == null ? null : order.user.eq(user);
    }

    private BooleanExpression notOrderStatusRequest(OrderStatus orderStatus) {
        return StringUtils.isNullOrEmpty(orderStatus.name())
            ? null
            : order.orderStatus.eq(orderStatus);
    }

    private BooleanExpression isSubscription(boolean subscription) {
        return order.subscription.eq(subscription);
    }

    private OrderSpecifier<Long> getSort(OrderPageRequest pageRequest) {
        return pageRequest.getSort() == SortType.DESC ? order.id.desc() : order.id.asc();
    }
}
