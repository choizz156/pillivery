package com.team33.modulecore.core.order.infra;

import static com.team33.modulecore.core.order.domain.OrderStatus.*;
import static com.team33.modulecore.core.order.domain.entity.QOrder.*;
import static com.team33.modulecore.core.order.domain.entity.QOrderItem.*;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPageRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class OrderQueryDslDao implements OrderQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Order> findOrders(
		OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition
	) {
		List<Order> contents = queryFactory
			.selectFrom(order)
			.where(
				userEq(orderFindCondition.getUserId()),
				notOrderStatusRequest(orderFindCondition.getOrderStatus())
			)
			.limit(pageRequest.getSize())
			.offset(pageRequest.getOffset())
			.orderBy(getOrderSort(pageRequest.getSort()))
			.fetch();

		if (contents.isEmpty()) {
			return Page.empty();
		}

		JPAQuery<Long> countQuery = queryFactory
			.select(order.count())
			.from(order)
			.where(
				userEq(orderFindCondition.getUserId()),
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
	public List<OrderItem> findSubscriptionOrderItems(
		OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition
	) {

		List<OrderItem> fetch = queryFactory
			.select(orderItem)
			.from(orderItem)
			.innerJoin(orderItem.order, order)
			.where(
				orderUserAndOrderItemUserEq(),
				subscriptionOrderStatusEq(orderFindCondition.getOrderStatus())
			)
			.limit(pageRequest.getSize())
			.offset(pageRequest.getOffset())
			.orderBy(getSubscriptionOrderSort(pageRequest.getSort()))
			.fetch();

		if (fetch.isEmpty()) {
			return List.of();
		}

		return Collections.unmodifiableList(fetch);
	}

	@Override
	public OrderItem findSubscriptionOrderItemBy(long id) {
		OrderItem fetch = queryFactory
			.select(orderItem)
			.from(orderItem)
			.where(orderItem.id.eq(id))
			.fetchOne();

		if (fetch == null) {
			throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
		}

		return fetch;
	}

	@Override
	public Order findById(long id) {
		Order fetch = queryFactory.selectFrom(order).where(order.id.eq(id)).fetchOne();

		if (fetch == null) {
			throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
		}
		return fetch;
	}

	@Override
	public boolean findIsSubscriptionById(long orderId) {
		return Boolean.TRUE.equals(
			queryFactory.select(order.isSubscription).from(order).where(order.id.eq(orderId)).fetchOne()
		);
	}

	@Override
	public String findTid(long orderId) {
		return queryFactory.select(order.paymentCode.tid).from(order).where(order.id.eq(orderId)).fetchOne();
	}

	private BooleanExpression orderUserAndOrderItemUserEq() {
		return orderItem.order.userId.eq(order.userId);
	}

	private BooleanExpression subscriptionOrderStatusEq(OrderStatus orderStatus) {
		return orderStatus == SUBSCRIBE
			? order.orderStatus.eq(orderStatus)
			: null;
	}

	private BooleanExpression userEq(long userId) {
		return order.userId.eq(userId);
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
