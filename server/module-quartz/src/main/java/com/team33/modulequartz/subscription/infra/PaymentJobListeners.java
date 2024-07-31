package com.team33.modulequartz.subscription.infra;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.springframework.context.ApplicationEventPublisher;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulequartz.subscription.domain.PaymentDateUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PaymentJobListeners implements JobListener {

	private final ApplicationEventPublisher applicationEventPublisher;

	private static final String PAYMENT_JOB = "payment Job";
	private static final String RETRY = "retry";

	@Override
	public String getName() {
		return PAYMENT_JOB;
	}

	@Override
	public void jobToBeExecuted(final JobExecutionContext context) {
	}

	/**
	 * job 중단 시
	 *
	 * @param context
	 */
	@Override
	public void jobExecutionVetoed(final JobExecutionContext context) {
		JobKey key = context.getJobDetail().getKey();
		log.error("중단된 job의 jobkey = {}", key);
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
		int retryCount = jobDataMap.getInt(RETRY);

		retryIfJobException(jobException, jobDataMap, retryCount);
		deleteJobIfJobExceptionMoreThan2(context.getJobDetail().getKey(), retryCount, jobException);
		// updatePaymentDay(context, jobDataMap);

		long orderItemId = jobDataMap.getLong("orderItemId");
		applicationEventPublisher.publishEvent(new PaymentDateUpdatedEvent(orderItemId));
	}

	private void deleteJobIfJobExceptionMoreThan2(JobKey key, int retryCount, JobExecutionException jobException) {
		if(retryCount >= 2){
				jobException.setUnscheduleAllTriggers(true);
				log.error("job 예외로 인한 스케쥴 취소 = {}, 재시도 횟수 = {}, 메시지 = {}", key, retryCount, jobException.getMessage());
				throw new BusinessLogicException(ExceptionCode.SCHEDULE_CANCEL);
		}
	}

	private void retryIfJobException(
		final JobExecutionException jobException,
		final JobDataMap jobDataMap,
		final int retryCount
	) {
		if (jobException != null) {
			log.warn("job exception = {}, key = {}", jobException.getMessage(), jobDataMap.getKeys());
			retryImmediately(jobException, jobDataMap, retryCount);
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

	// private void updatePaymentDate(final OrderItem orderItem) {
	// 	ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
	// 	orderItemService.updateNextPaymentDate(paymentDay, orderItem);
	// }

	// private void cancelSchedule(final JobExecutionContext context) {
	// 	log.error("job 예외로 인한 스케쥴 취소");
	//
	// 	try {
	// 		JobKey key = context.getJobDetail().getKey();
	// 		context.getScheduler().deleteJob(key);
	// 		//TODO: 취소된것도 조회가되나???
	// 		// throw new BusinessLogicException(ExceptionCode.PAYMENT_FAIL);
	// 	} catch (SchedulerException e) {
	// 		log.error("스케쥴 삭제 실패 = {}, key = {}", e.getMessage(), context.getJobDetail().getKey());
	// 	}
	// }

	private void retryImmediately(
		final JobExecutionException jobException,
		final JobDataMap jobDataMap,
		int retryCount
	) {
		if (retryCount < 2) {
			log.warn("최초 재시도");
			jobException.setRefireImmediately(true);
			jobDataMap.put(RETRY, ++retryCount);
		}
	}
}
