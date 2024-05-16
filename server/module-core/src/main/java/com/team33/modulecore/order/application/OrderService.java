package com.team33.modulecore.order.application;

import static com.team33.modulecore.order.domain.OrderStatus.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.cart.application.NormalCartService;
import com.team33.modulecore.cart.application.SubscriptionCartService;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.order.dto.OrderContext;
import com.team33.modulecore.user.domain.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final SubscriptionCartService subscriptionCartService;
	private final NormalCartService normalCartService;
	private final UserFindHelper userFindHelper;

	public Order callOrder(List<OrderItem> orderItems, OrderContext orderContext) {
		Order order = createOrder(orderItems, orderContext);
		return orderRepository.save(order);
	}

	public void cancelOrder(Long orderId) {
		Order order = findOrder(orderId);

		order.changeOrderStatus(CANCEL);

		// order.getOrderItems()
		//     .forEach(orderItem -> orderItem.getItem().minusSales(orderItem.getQuantity()));
	}

	public void changeOrderStatusToComplete(Long orderId) {
		Order order = findOrder(orderId);

		order.changeOrderStatus(OrderStatus.COMPLETE);
		if(order.isOrderedAtCart()){
			normalCartService.refresh(order.getUser().getCartId(), order.getOrderItems());
		}
	}

	public void changeOrderStatusToSubscribe(Long orderId, String sid) {
		Order order = findOrder(orderId);

		order.addSid(sid);
		order.changeOrderStatus(OrderStatus.SUBSCRIBE);

		if(order.isOrderedAtCart()){
			subscriptionCartService.refresh(order.getUser().getCartId(), order.getOrderItems());
		}
	}

	public Order deepCopy(Order order) {
		Order newOrder = new Order(order);
		orderRepository.save(newOrder);

		return newOrder;
	}

	public void changeSubscriptionItemQuantity(long orderId, long orderItemId, int quantity) {
		Order findOrder = findOrder(orderId);

		findOrder.getOrderItems().stream()
			.filter(orderItem -> orderItem.getId() == orderItemId)
			.findFirst()
			.ifPresentOrElse(
				orderItem -> orderItem.changeQuantity(quantity),
				() -> {
					throw new BusinessLogicException(ExceptionCode.NOT_ORDERED_ITEM);
				}
			);
	}

	public Order findOrder(long orderId) {
		return orderRepository.findById(orderId)
			.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
	}

	//    private void plusSalesOfItem(OrderItem oi) {
	//        oi.getItem().plusSales(oi.getItem().getSales() + oi.getQuantity());
	//    }
	//Todo: 주문 완료되면 판매량을 올려야함.

	private Order createOrder(List<OrderItem> orderItems, OrderContext orderContext) {
		User user = userFindHelper.findUser(orderContext.getUserId());
		return Order.create(orderItems, orderContext, user);
	}

}
