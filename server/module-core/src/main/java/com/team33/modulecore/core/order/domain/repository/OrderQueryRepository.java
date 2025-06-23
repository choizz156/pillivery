package com.team33.modulecore.core.order.domain.repository;

import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.dto.OrderFindCondition;
import com.team33.modulecore.core.order.dto.OrderPageRequest;
import com.team33.modulecore.core.order.dto.query.OrderItemQueryDto;
import com.team33.modulecore.core.order.dto.query.OrderQueryDto;
import com.team33.modulecore.core.order.dto.query.SubscriptionOrderItemQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

public interface OrderQueryRepository {

	Page<OrderItemQueryDto> findOrdersWithItems(
		OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition);

	Page<SubscriptionOrderItemQueryDto> findSubscriptionOrderItemsWithItems(
		OrderPageRequest pageRequest,
		OrderFindCondition orderFindCondition);

	OrderItem findSubscriptionOrderItemBy(long id);

	OrderQueryDto findById(@Param("id") long id);

	boolean findIsSubscriptionById(@Param("orderId") long orderId);

	String findTid(@Param("orderId") long orderId);
}
