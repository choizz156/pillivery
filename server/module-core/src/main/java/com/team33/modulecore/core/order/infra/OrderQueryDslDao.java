package com.team33.modulecore.core.order.infra;

import static com.team33.modulecore.core.item.domain.entity.QItem.item;
import static com.team33.modulecore.core.order.domain.OrderStatus.SUBSCRIPTION;
import static com.team33.modulecore.core.order.domain.entity.QOrder.order;
import static com.team33.modulecore.core.order.domain.entity.QOrderItem.orderItem;
import static com.team33.modulecore.core.order.domain.entity.QSubscriptionOrder.subscriptionOrder;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.core.item.domain.entity.QItem;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.entity.QOrder;
import com.team33.modulecore.core.order.domain.entity.QOrderItem;
import com.team33.modulecore.core.order.domain.entity.QSubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPageRequest;
import com.team33.modulecore.core.order.dto.query.OrderItemQueryDto;
import com.team33.modulecore.core.order.dto.query.OrderItemSimpleQueryDto;
import com.team33.modulecore.core.order.dto.query.OrderQueryDto;
import com.team33.modulecore.core.order.dto.query.QOrderItemQueryDto;
import com.team33.modulecore.core.order.dto.query.QOrderItemSimpleQueryDto;
import com.team33.modulecore.core.order.dto.query.QOrderQueryDto;
import com.team33.modulecore.core.order.dto.query.QSubscriptionOrderItemQueryDto;
import com.team33.modulecore.core.order.dto.query.SubscriptionOrderItemQueryDto;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class OrderQueryDslDao implements OrderQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<OrderItemQueryDto> findOrdersWithItems(OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition) {

		QOrderItem oi = orderItem;
		QItem i = item;
		QOrder o = order;

		List<OrderItemQueryDto> fetch = queryFactory.select(new QOrderItemQueryDto(
				o.id,
				oi.id,
				oi.quantity,
				i.id,
				i.information.productName,
				i.information.price.realPrice,
				i.information.mainFunction,
				i.information.image.thumbnail,
				i.categories
			))
			.from(o)
			.innerJoin(o.orderItems, oi)
			.innerJoin(oi.item, i)
			.where(
				orderStatusEq(orderFindCondition.getOrderStatus()),
				orderUserEq(orderFindCondition.getUserId())
			)
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getSize())
			.orderBy(getOrderSort(pageRequest.getSort()))
			.fetch();

		if (fetch.isEmpty()) {
			return Page.empty();
		}

		JPAQuery<Long> countQuery = queryFactory
			.select(orderItem.count())
			.from(order)
			.join(order.orderItems, orderItem)
			.where(
				orderStatusEq(orderFindCondition.getOrderStatus()),
				orderUserEq(orderFindCondition.getUserId())
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(
				pageRequest.getPage() - 1,
				pageRequest.getSize(),
				Sort.by(pageRequest.getSort(), "id")
			),
			countQuery::fetchOne);
	}

	@Override
	public Page<SubscriptionOrderItemQueryDto> findSubscriptionOrderItemsWithItems(
		OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition
	) {

		QOrderItem oi = orderItem;
		QItem i = item;
		QSubscriptionOrder so = subscriptionOrder;

		List<SubscriptionOrderItemQueryDto> fetch = queryFactory.select(
				new QSubscriptionOrderItemQueryDto(so, oi, i))
			.from(so)
			.innerJoin(so.orderItem, oi)
			.innerJoin(oi.item, i)
			.where(
				subscriptionOrderStatusEq(orderFindCondition.getOrderStatus()),
				subscriptionOrderUserUserEq(orderFindCondition.getUserId())
			)
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getSize())
			.orderBy(getSubscriptionOrderSort(pageRequest.getSort()))
			.fetch();

		if (fetch.isEmpty()) {
			return Page.empty();
		}

		JPAQuery<Long> countQuery = queryFactory
			.select(orderItem.count())
			.from(subscriptionOrder)
			.join(subscriptionOrder.orderItem, orderItem)
			.where(
				subscriptionOrderStatusEq(orderFindCondition.getOrderStatus()),
				subscriptionOrderUserUserEq(orderFindCondition.getUserId())
			);

		return PageableExecutionUtils.getPage(
			fetch,
			PageRequest.of(
				pageRequest.getPage() - 1,
				pageRequest.getSize(),
				Sort.by(pageRequest.getSort(), "id")
			),
			countQuery::fetchOne);
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
	public OrderQueryDto findById(long id) {

		OrderQueryDto orderQueryDto = queryFactory.select(new QOrderQueryDto(
				order.id,
				order.totalItemsCount,
				order.orderCommonInfo.price.totalPrice,
				order.orderCommonInfo.price.totalDiscountPrice,
				order.orderCommonInfo.price.expectPrice,
				order.orderCommonInfo.orderStatus,
				order.createdAt,
				order.updatedAt,
				order.orderCommonInfo.receiver,
				order.orderCommonInfo.totalQuantity
			))
			.from(order)
			.where(order.id.eq(id))
			.fetchOne();

		if (orderQueryDto == null) {
			throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
		}

		List<OrderItemSimpleQueryDto> orderItemSimpleQueryDtos = queryFactory.select(
				new QOrderItemSimpleQueryDto(
					orderItem.id,
					orderItem.quantity,
					item.id,
					item.information.enterprise,
					item.information.productName,
					item.information.price.originPrice,
					item.information.price.realPrice,
					item.information.price.discountRate,
					item.information.price.discountPrice
				)).from(orderItem)
			.join(orderItem.item, item)
			.where(orderItem.order.id.eq(id))
			.fetch();

		orderQueryDto.setOrderItems(orderItemSimpleQueryDtos);

		return orderQueryDto;
	}

	@Override
	public boolean findIsSubscriptionById(long subscriptionOrderId) {

		return Boolean.TRUE.equals(
			queryFactory.select(subscriptionOrder.subscriptionInfo.subscription)
				.from(subscriptionOrder)
				.where(subscriptionOrder.id.eq(subscriptionOrderId))
				.fetchOne());
	}

	@Override
	public String findTid(long orderId) {

		return queryFactory.select(order.orderCommonInfo.paymentToken.tid).from(order)
			.where(order.id.eq(orderId))
			.fetchOne();
	}

	private BooleanExpression subscriptionOrderUserUserEq(long userId) {

		return subscriptionOrder.orderCommonInfo.userId.eq(userId);
	}

	private BooleanExpression subscriptionOrderStatusEq(OrderStatus orderStatus) {

		return orderStatus == SUBSCRIPTION
			? subscriptionOrder.orderCommonInfo.orderStatus.eq(orderStatus)
			: null;
	}

	private BooleanExpression orderUserEq(long userId) {

		return order.orderCommonInfo.userId.eq(userId);
	}

	private BooleanExpression orderStatusEq(OrderStatus orderStatus) {

		return order.orderCommonInfo.orderStatus.eq(orderStatus);
	}

	private OrderSpecifier<Long> getOrderSort(Direction pageRequest) {

		return pageRequest == Direction.DESC ? order.id.desc() : order.id.asc();
	}

	private OrderSpecifier<Long> getSubscriptionOrderSort(Direction pageRequest) {

		return pageRequest == Direction.DESC ? orderItem.id.desc() : orderItem.id.asc();
	}
}

