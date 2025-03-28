package com.team33.modulequartz.subscription.application;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulequartz.subscription.infra.PaymentJobListeners;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubscriptionService {

	public static final Logger log = LoggerFactory.getLogger("fileLog");

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
			.filter(orderItem -> orderItem.getSubscriptionOrder().isSubscription())
			.forEach(orderItem -> startSchedule(orderId, orderItem));
	}

	public Trigger changeTrigger(long orderId, long itemOrderId) {

		OrderItem orderItem = orderItemService.findOrderItem(itemOrderId);

		JobKey jobKey = JobKeyGenerator.build(orderId, orderItem.getItem().getProductName());
		Trigger newTrigger = triggerService.build(jobKey, orderItem);

		changeTrigger(newTrigger);
		return newTrigger;
	}

	@Transactional
	public void cancelScheduler(long orderId, long itemOrderId) {
		OrderItem orderItem = orderItemService.findOrderItem(itemOrderId);

		deleteSchedule(orderId, orderItem.getItem().getProductName());
	}

	private void changeTrigger(Trigger newTrigger) {
		try {
			TriggerKey oldKey = newTrigger.getKey();
			scheduler.rescheduleJob(oldKey, newTrigger);
			log.info("스케쥴 변경 => {}, 새로운 트리거 시작 시간 => {}", oldKey, newTrigger.getStartTime());
		} catch (SchedulerException e) {
			throw new BusinessLogicException(e.getMessage());
		}
	}

	private void deleteSchedule(long orderId, String productName) {
		JobKey jobkey = JobKeyGenerator.build(orderId, productName);
		try {
			scheduler.deleteJob(jobkey);
			log.info("스케쥴 삭제 => {}", jobkey);
		} catch (SchedulerException e) {
			log.info("스케쥴 삭제 실패 job => {}", e.getMessage());
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
			ListenerManager listenerManager = scheduler.getListenerManager();
			listenerManager.addJobListener(
				new PaymentJobListeners(applicationEventPublisher)
			);
			scheduler.scheduleJob(jobDetail, lastTrigger);
		} catch (SchedulerException e) {
			log.warn("스케쥴 등록 실패 job => {},{}", jobDetail.getKey().getName(), e.getMessage());
		}
	}

}
