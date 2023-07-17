package com.team33.modulequartz.subscription.job;

import static org.quartz.JobKey.jobKey;

import com.team33.modulecore.domain.order.entity.ItemOrder;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.service.ItemOrderService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.global.exception.BusinessLogicException;
import com.team33.modulecore.global.exception.ExceptionCode;
import com.team33.modulequartz.subscription.trigger.TriggerService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

@Slf4j
@RequiredArgsConstructor
public class JobListeners implements JobListener {

    private final TriggerService triggerService;
    private final ItemOrderService itemOrderService;
    private final OrderService orderService;
    private final JobDetailService jobDetailService;

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
        ItemOrder itemOrder = (ItemOrder) jobDataMap.get("itemOrder");
        Long orderId = (Long) jobDataMap.get("orderId");

        ItemOrder newItemOrder = updateDeliveryDate(itemOrder);
        JobDetail jobDetail = newJob(newItemOrder, orderId);
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

    private JobDetail newJob(final ItemOrder itemOrder, final Long orderId) {
        Order newOrder = getOrder(orderId);
        ItemOrder newItemOrder = itemOrderService.itemOrderCopy(orderId, newOrder, itemOrder);
        return updateJobDetails(itemOrder, newOrder, newItemOrder);
    }

    private JobDetail updateJobDetails(
        ItemOrder itemOrder,
        Order newOrder,
        ItemOrder newItemOrder
    ) {
        User user = newOrder.getUser();
        JobKey jobkey =
            jobKey(user.getUserId() + itemOrder.getItem().getTitle(),
                String.valueOf(user.getUserId())
            );
        return jobDetailService.build(jobkey, newOrder.getOrderId(), newItemOrder);
    }

    private Order getOrder(Long orderId) {
        orderService.completeOrder(orderId);
        Order order = orderService.findOrder(orderId);
        return orderService.deepCopy(order);
    }

    private ItemOrder updateDeliveryDate(final ItemOrder itemOrder) {
        ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("payment = {}", paymentDay);
        ZonedDateTime nextDelivery = paymentDay.plusDays(itemOrder.getPeriod());
        return itemOrderService.updateDeliveryInfo(paymentDay, nextDelivery, itemOrder);
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
