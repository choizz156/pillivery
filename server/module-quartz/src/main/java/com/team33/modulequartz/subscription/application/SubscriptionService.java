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
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.order.domain.entity.OrderItem;
import com.team33.modulequartz.subscription.infra.PaymentJobListeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {

	private final Scheduler scheduler;
	private final TriggerService triggerService;
	private final JobDetailService jobDetailService;
	private final OrderItemService orderItemService;
	private final OrderFindHelper orderFindHelper;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional(readOnly = true)
	public void applySchedule(long orderId) {
		Order order = orderFindHelper.findOrder(orderId);
		order.getOrderItems().stream()
			.filter(OrderItem::isSubscription)
			.forEach(orderItem -> startSchedule(orderId, orderItem));
	}

	@Transactional
	public OrderItem changePeriod(long orderId, int period, long itemOrderId) {

		OrderItem orderItem = orderItemService.findOrderItem(itemOrderId);

		orderItemService.changeItemPeriod(period, orderItem);

		JobKey jobKey = JobKeyGenerator.build(orderId, orderItem.getItem().getProductName());
		Trigger newTrigger = triggerService.build(jobKey, orderItem);

		changeTrigger(newTrigger);

		return orderItem;
	}

	@Transactional
	public void cancelScheduler(long orderId, long itemOrderId) {
		OrderItem orderItem = orderItemService.findOrderItem(itemOrderId);

		deleteSchedule(orderId, orderItem.getItem().getProductName());
	}

	private void changeTrigger(Trigger newTrigger) {
		try {
			scheduler.rescheduleJob(newTrigger.getKey(), newTrigger);
		} catch (SchedulerException e) {
			throw new BusinessLogicException(e.getMessage());
		}
	}

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

	private void startSchedule(long orderId, OrderItem orderItem) {

		JobKey jobKey = JobKeyGenerator.build(orderId, orderItem.getProductName());

		JobDetail jobDetail = jobDetailService.build(jobKey, orderId, orderItem.getId());
		Trigger lastTrigger = triggerService.build(jobKey, orderItem);

		toSchedule(jobDetail, lastTrigger);
	}

	private void toSchedule(JobDetail jobDetail, Trigger lastTrigger) {
		try {
			applyJobListener();
			scheduler.scheduleJob(jobDetail, lastTrigger);
		} catch (SchedulerException e) {
			log.error("스케쥴 등록 실패 job => {},{}", jobDetail.getKey().getName(), e.getMessage());
		}
	}

	private void applyJobListener() throws SchedulerException {
		ListenerManager listenerManager = scheduler.getListenerManager();
		listenerManager.addJobListener(
			new PaymentJobListeners(applicationEventPublisher)
		);
	}
}
