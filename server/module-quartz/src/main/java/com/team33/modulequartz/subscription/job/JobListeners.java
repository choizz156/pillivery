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

    private static final String RETRY = "retry";

    @Override
    public String getName() {
        return "payment Job";
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
     * job 실행 후 로직 2번 job 수행 실패 시 스케쥴을 취소합니다.
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
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        int retryCount = (int) jobDataMap.get(RETRY);
        log.info("실행된 job의 jobkey = {}", key);

        retryOrDeleteIfJobException(context, jobException, key, jobDataMap, retryCount);

        updateSchedule(context, jobDataMap);
    }

    private void updateSchedule(
        final JobExecutionContext context,
        final JobDataMap jobDataMap
    ) {
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
            throw new BusinessLogicException(e.getMessage());
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
        final JobKey key,
        final JobDataMap jobDataMap,
        final int retryCount
    ) {
        if (jobException != null) {
            log.warn("job exception = {}", jobException.getMessage());
            retryImmediately(jobException, jobDataMap, retryCount);
            retryIn3Hours(context, jobDataMap, retryCount);
            deleteJob(context, jobException, key, retryCount);
        }
    }

    private void deleteJob(
        final JobExecutionContext context,
        final JobExecutionException jobException,
        final JobKey key,
        final int retryCount
    ) {
        if (retryCount == 2) {
            log.error("스케쥴링 취소 = {}", key);
            jobException.setUnscheduleAllTriggers(true);
            deleteSchedule(context, key);
        }
    }

    private void deleteSchedule(final JobExecutionContext context, final JobKey key) {
        try {
            context.getScheduler().deleteJob(key);
            throw new BusinessLogicException(ExceptionCode.SCHEDULE_CANCEL);
        } catch (SchedulerException e) {
            throw new BusinessLogicException(e.getMessage());
        }
    }

    private void retryIn3Hours(
        final JobExecutionContext context,
        final JobDataMap jobDataMap,
        int retryCount
    ) {
        if (retryCount == 1) {
            jobDataMap.put(RETRY, ++retryCount);
            Trigger trigger = triggerService.retryTrigger();
            reschedule(context, trigger);
        }
    }

    private void reschedule(final JobExecutionContext context, final Trigger trigger) {
        try {
            context.getScheduler().rescheduleJob(new TriggerKey("lazy retry"), trigger);
        } catch (SchedulerException e) {
            throw new BusinessLogicException(e.getMessage());
        }
    }

    private void retryImmediately(
        final JobExecutionException jobException,
        final JobDataMap jobDataMap,
        int retryCount
    ) {
        if (retryCount == 0) {
            jobDataMap.put(RETRY, ++retryCount);
            jobException.refireImmediately();
        }
    }

}
