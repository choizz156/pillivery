package com.team33.modulequartz.subscription.application;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.order.application.OrderCreateService;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.application.OrderStatusService;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulequartz.subscription.infra.PaymentJobListeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionService {

	private final Scheduler scheduler;
	private final TriggerService trigger;
	private final JobDetailService jobDetailService;
	private final OrderCreateService orderCreateService;
	private final OrderStatusService orderStatusService;
	private final OrderItemService orderItemService;
	private final OrderQueryService orderQueryService;
	private final OrderFindHelper orderFindHelper;
	private final ApplicationEventPublisher applicationEventPublisher;

	public void applySchedule(long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		order.getOrderItems().stream()
			.filter(OrderItem::isSubscription)
			.forEach(orderItem -> applySchedule(orderId, orderItem));
	}

	@Transactional
	public OrderItem changePeriod(long orderId, int period, long itemOrderId) {

		OrderItem orderItem = orderItemService.findOrderItem(itemOrderId);

		orderItemService.changeItemPeriod(period, orderItem);

		//TODO: 트리거 변경 로직
		Trigger newTrigger = trigger.build(JobKeyGenerator.build(orderId, orderItem.getItem().getProductName()), orderItem);

		changeTrigger(newTrigger);

		return orderItem;
	}

	@Transactional
	public void cancelScheduler(long orderId, long itemOrderId) {
		log.info("cancelScheduler");
		// Order order = orderQueryService.findOrder(orderId);
		OrderItem orderItem =orderItemService.findOrderItem(itemOrderId);

		deleteSchedule(orderId, orderItem.getItem().getProductName());
		// orderItemService.cancelItemOrder(orderId, orderItem);
		log.info("canceled item title = {}", orderItem.getItem().getProductName());
	}

	private void changeTrigger(Trigger newTrigger) {
		try {
			scheduler.rescheduleJob(newTrigger.getKey(), newTrigger);
		} catch (SchedulerException e) {
			throw new BusinessLogicException(e.getMessage());
		}
	}

	// private OrderItem getChangedItemOrder(final Order order, final OrderItem orderItem) {
	// 	var paymentDay = orderItem.getPaymentDay();
	// 	var nextDelivery = paymentDay.plusDays(orderItem.getPeriod());
	// 	OrderItem updatedOrderItem =
	// 		orderItemService.updateDeliveryInfo(paymentDay, nextDelivery, orderItem);
	// 	log.info("{}", updatedOrderItem.getPaymentDay());
	// 	extendPeriod(order, updatedOrderItem);
	//
	// 	paymentDay.plusDays(orderItem.getPeriod());
	// 	return updatedOrderItem;

	// }
	/**
	 * 만약 기간을 변경할 경우, 다음 결제 날짜가 현재보다 이전이면, 즉, 기간을 줄이면 기존 결제 예정일에 결제 후 주기 변경
	 *
	 */

	// private void deleteSchedule(Order order, OrderItem orderItem) {
	// 	log.info("delete schedule");
	// 	User user = userFindHelper.findUser(order.getUserId());
	// 	deleteSchedule(orderItem, user);
	// }

	private void deleteSchedule(long orderId, String productName) {
		JobKey jobkey = JobKeyGenerator.build(orderId, productName);
		deleteSchedule(jobkey);
	}

	private void deleteSchedule(JobKey jobkey) {
		try {
			scheduler.deleteJob(jobkey);
		} catch (SchedulerException e) {
			log.error("스케쥴 삭제 실패 job => {}", e.getMessage());
		}
	}

	private void applySchedule(long orderId, OrderItem orderItem) {

		JobKey jobKey = JobKeyGenerator.build(orderId, orderItem.getItem().getProductName());

		// JobDetail paymentDay = jobDetailService.build(jobkey, orderId, orderItem);
		// Trigger lastTrigger = trigger.build(jobkey, orderItem);

		JobDetail jobDetail = jobDetailService.build(jobKey, orderId);
		Trigger lastTrigger = trigger.build(jobKey, orderItem);
		schedule(jobDetail, lastTrigger);
	}

	private void schedule(JobDetail jobDetail, Trigger lastTrigger) {
		try {
			ListenerManager listenerManager = scheduler.getListenerManager();
			listenerManager.addJobListener(
				new PaymentJobListeners(
					applicationEventPublisher,
					trigger,
					orderItemService,
					orderCreateService,
					orderStatusService,
					jobDetailService,
					orderQueryService
				)
			);

			scheduler.scheduleJob(jobDetail, lastTrigger);
		} catch (SchedulerException e) {
			log.error("스케쥴 등록 실패 job => {},{}", jobDetail.getKey().getName());
		}
	}
}
