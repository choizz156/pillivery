package com.team33.modulequartz.subscription.infra;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.springframework.context.ApplicationEventPublisher;

import com.team33.modulecore.order.application.OrderCreateService;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.application.OrderStatusService;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulequartz.subscription.application.JobDetailService;
import com.team33.modulequartz.subscription.application.TriggerService;
import com.team33.modulequartz.subscription.domain.PaymentDateUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PaymentJobListeners implements JobListener {

	private final ApplicationEventPublisher applicationEventPublisher;
	private final TriggerService triggerService;
	private final OrderItemService orderItemService;
	private final OrderCreateService orderCreateService;
	private final OrderStatusService paymentService;
	private final JobDetailService jobDetailService;
	private final OrderQueryService orderQueryService;

	private static final String PAYMENT_JOB = "payment Job";
	private static final String RETRY = "retry";

	@Override
	public String getName() {
		return PAYMENT_JOB;
	}

	/**
	 * job 수행전
	 *
	 * @param context
	 */
	@Override
	public void jobToBeExecuted(final JobExecutionContext context) {
		if (context.getJobDetail() == null) {
			log.info("start job");
		}
		JobKey key = context.getJobDetail().getKey();
		log.info("실행될 job의 jobkey = {}", key);
	}

	/**
	 * job 중단 시
	 *
	 * @param context
	 */
	@Override
	public void jobExecutionVetoed(final JobExecutionContext context) {
		JobKey key = context.getJobDetail().getKey();
		log.info("중단된 job의 jobkey = {}", key);
	}

	/**
	 * job 실행 후 예외가 발생할 경우,
	 * - 첫 번째 예외 발생 시, 바로 job을 재실행한다.
	 * - 두 번째 예외 발생 시, 한 시간 후에 다시 재시도.
	 * 예외가 발생하지 않은 경우, 다음 job을 등록한다.
	 *
	 * @param context
	 * @param jobException
	 */
	@Override
	public void jobWasExecuted(
		final JobExecutionContext context,
		final JobExecutionException jobException
	) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		int retryCount = (int)jobDataMap.get(RETRY);

		retryOrDeleteIfJobException(context, jobException, jobDataMap, retryCount);
		// updatePaymentDay(context, jobDataMap);

		OrderItem orderItem = (OrderItem)jobDataMap.get("orderItem");
		applicationEventPublisher.publishEvent(new PaymentDateUpdatedEvent(orderItem));
	}

	private void retryOrDeleteIfJobException(
		final JobExecutionContext context,
		final JobExecutionException jobException,
		final JobDataMap jobDataMap,
		final int retryCount
	) {
		if (jobException != null) {
			log.warn("job exception = {}", jobException.getMessage());
			retryImmediately(jobException, jobDataMap, retryCount);
			// cancelSchedule(context, retryCount);
		}
	}

	// private void updatePaymentDay(
	// 	final JobExecutionContext context,
	// 	final JobDataMap jobDataMap
	// ) {
	//
	// 	OrderItem orderItem = (OrderItem)jobDataMap.get("orderItem");
		// Long orderId = (Long)jobDataMap.get("orderId");

		 // updatePaymentDate(orderItem);
		// JobDetail jobDetail = createNewJob(newOrderItem, orderId);
		// triggerService.build(context.getJobDetail().getKey(), orderItem);
		// replaceJob(context, jobDetail);
	// }

	// private void replaceJob(final JobExecutionContext context, final JobDetail jobDetail) {
	// 	try {
	// 		context.getScheduler().addJob(jobDetail, true);
	// 		log.info("스케쥴 업데이트 완료");
	// 	} catch (SchedulerException e) {
	// 		log.error("스케쥴 업데이트 실패");
	// 	}
	// }
	//
	// private JobDetail createNewJob(OrderItem newOrderItem, long orderId) {
	//
	// 	JobKey jobkey =
	// 		jobKey(orderId + newOrderItem.getItem().getProductName(),
	// 			String.valueOf(orderId)
	// 		);
	//
	// 	return jobDetailService.build(jobkey, orderId, newOrderItem);
	// }
	//
	//
	// private Order getOrder(Long orderId) {
	// 	Order order = orderQueryService.findOrder(orderId);
	// 	return orderCreateService.deepCopy(order);
	// }

	private void updatePaymentDate(final OrderItem orderItem) {
		ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		orderItemService.updateNextPaymentDate(paymentDay, orderItem);
	}

	// private void cancelSchedule(final JobExecutionContext context, final int retryCount) {
	// 	if (retryCount >= 2) {
	// 		log.error("job 예외로 인한 스케쥴 취소");
	//
	// 		try {
	// 			JobKey key = context.getJobDetail().getKey();
	// 			context.getScheduler().deleteJob(key);
	// 			//TODO: 취소된것도 조회가되나???
	// 			// throw new BusinessLogicException(ExceptionCode.PAYMENT_FAIL);
	// 		} catch (SchedulerException e) {
	// 			log.error("스케쥴 삭제 실패 = {}, key = {}", e.getMessage(), context.getJobDetail().getKey());
	// 		}
	// 	}
	// }

	private void retryImmediately(
		final JobExecutionException jobException,
		final JobDataMap jobDataMap,
		int retryCount
	) {
		if (retryCount  < 2) {
			log.warn("최초 재시도");
			jobDataMap.put(RETRY, ++retryCount);
			jobException.setRefireImmediately(true);
		}
	}
}
