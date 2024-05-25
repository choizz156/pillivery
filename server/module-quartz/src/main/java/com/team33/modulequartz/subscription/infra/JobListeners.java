package com.team33.modulequartz.subscription.infra;

import static org.quartz.JobKey.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.application.OrderCreateService;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.application.OrderStatusService;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.user.domain.entity.User;
import com.team33.modulequartz.subscription.application.JobDetailService;
import com.team33.modulequartz.subscription.application.TriggerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JobListeners implements JobListener {

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
     * job 실행 후 예외가 발생할 경우, - 첫 번째 예외 발생 시, 바로 job을 재실행한다. - 두 번째 예외 발생 시, 특정 시간을 간격으로 job을 재실행한다.
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
        JobKey key = context.getJobDetail().getKey();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        int retryCount = (int) jobDataMap.get(RETRY);
        log.info("실행된 job의 jobkey = {}", key);

        retryOrDeleteIfJobException(context, jobException, jobDataMap, retryCount);
        updateSchedule(context, jobDataMap);
    }

    private void updateSchedule(
        final JobExecutionContext context,
        final JobDataMap jobDataMap
    ) {
        log.info("새로운 job 업데이트");
        OrderItem orderItem = (OrderItem) jobDataMap.get("itemOrder");
        Long orderId = (Long) jobDataMap.get("orderId");

        OrderItem newOrderItem = updateDeliveryDate(orderItem);
        JobDetail jobDetail = newJob(newOrderItem, orderId);
        replaceJob(context, jobDetail);
    }

    private void replaceJob(final JobExecutionContext context, final JobDetail jobDetail) {
        try {
            context.getScheduler().addJob(jobDetail, true);
            log.info("스케쥴 업데이트 완료");
        } catch (SchedulerException e) {
            log.error("스케쥴 업데이트 실패, 재시도 횟수 = {}");
        }
    }

    private JobDetail newJob(final OrderItem orderItem, final Long orderId) {
        Order newOrder = getOrder(orderId);
        OrderItem newOrderItem = orderItemService.itemOrderCopy(orderId, newOrder, orderItem);
        return updateJobDetails(orderItem, newOrder, newOrderItem);
    }

    private JobDetail updateJobDetails(
        OrderItem orderItem,
        Order newOrder,
        OrderItem newOrderItem
    ) {
        User user = newOrder.getUser();
        JobKey jobkey =
            jobKey(user.getId() + orderItem.getItem().getProductName(),
                String.valueOf(user.getId())
            );
        return jobDetailService.build(jobkey, newOrder.getId(), newOrderItem);
    }

    private Order getOrder(Long orderId) {
        paymentService.processOneTimeStatus(orderId);
        Order order = orderQueryService.findOrder(orderId);
        return orderCreateService.deepCopy(order);
    }

    private OrderItem updateDeliveryDate(final OrderItem orderItem) {
        ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("payment = {}", paymentDay);
        ZonedDateTime nextDelivery = paymentDay.plusDays(orderItem.getPeriod());
        return orderItemService.updateDeliveryInfo(paymentDay, nextDelivery, orderItem);
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
            retryIn24Hour(context, jobDataMap, retryCount);
            cancelSchedule(context, retryCount);
        }
    }

    private void cancelSchedule(final JobExecutionContext context, final int retryCount) {
        if (retryCount >= 4) {
            log.warn("job 예외로 인한 스케쥴 취소");
            try {
                JobKey key = context.getJobDetail().getKey();
                context.getScheduler().deleteJob(key);
                throw new BusinessLogicException(ExceptionCode.PAYMENT_FAIL);
            } catch (SchedulerException e) {
               log.error("스케쥴 삭제 실패, 재시도 횟수 = {}", retryCount );
            }
        }
    }

    private void retryIn24Hour(
        final JobExecutionContext context,
        final JobDataMap jobDataMap,
        int retryCount
    ) {
        if (retryCount == 1) {
            log.warn("재시도");
            jobDataMap.put(RETRY, ++retryCount);
            Trigger trigger = triggerService.retryTrigger();
            reschedule(context, trigger);
        }
    }

    private void retryImmediately(
        final JobExecutionException jobException,
        final JobDataMap jobDataMap,
        int retryCount
    ) {
        if (retryCount == 0) {
            log.warn("최초 재시도");
            jobDataMap.put(RETRY, ++retryCount);
            jobException.setRefireImmediately(true);
        }
    }

    private void reschedule(final JobExecutionContext context, final Trigger trigger) {
        try {
            log.warn("재시도 스케쥴 설정");
            context.getScheduler().rescheduleJob(
                new TriggerKey(trigger.getJobKey().getName(), trigger.getJobKey().getGroup()),
                trigger
            );
        } catch (SchedulerException e) {
            log.error("재시도 트리거 교체 실패");
        }
    }
}
