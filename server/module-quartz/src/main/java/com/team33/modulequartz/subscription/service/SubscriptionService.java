package com.team33.modulequartz.subscription.service;

import static org.quartz.JobKey.jobKey;

import com.team33.modulecore.domain.order.entity.ItemOrder;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.service.ItemOrderService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulequartz.subscription.job.JobDetailService;
import com.team33.modulequartz.subscription.job.JobListeners;
import com.team33.modulequartz.subscription.trigger.TriggerListeners;
import com.team33.modulequartz.subscription.trigger.TriggerService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final Scheduler scheduler;
    private final TriggerService trigger;
    private final JobDetailService jobDetailService;
    private final OrderService orderService;
    private final ItemOrderService itemOrderService;


    public void startSchedule(Order order, ItemOrder itemOrder) {
        applySchedule(order, itemOrder);
        log.info("orderId = {}, itemOrderId ={} ==> 스케쥴 설정완료", order.getOrderId());
    }

    @Transactional
    public ItemOrder changePeriod(Long orderId, Integer period, Long itemOrderId) {

        Order order = orderService.findOrder(orderId);
        ItemOrder itemOrder = findItemOrderInOrder(order, itemOrderId);

        itemOrderService.setItemPeriod(period, itemOrder);
        log.info("기존 아이템 결제 주기 ={}", itemOrder.getPeriod());

        if (isPaymentDirectly(order, period, itemOrder)) {
            itemOrder.getPaymentDay().plusDays(itemOrder.getPeriod());
            return itemOrder;
        }
        return getChangedItemOrder(order, itemOrder);
    }

//    @Transactional
//    public ItemOrder delayDelivery(Long orderId, Integer delay, Long itemOrderId) {
//        log.info("delay Delivery");
//        Order order = orderService.findOrder(orderId);
//        ItemOrder itemOrder =
//            itemOrderService.delayDelivery(
//                orderId, delay, findItemOrderInOrder(order, itemOrderId)
//            );
//        resetSchedule(order, itemOrder);
//        return itemOrder;
//    }

    @Transactional
    public void cancelScheduler(Long orderId, Long itemOrderId) {
        log.info("cancelScheduler");
        Order order = orderService.findOrder(orderId);
        ItemOrder itemOrder = getItemOrder(itemOrderId);

        deleteSchedule(order, itemOrder);
        itemOrderService.cancelItemOrder(orderId, itemOrder);
        log.info("canceled item title = {}", itemOrder.getItem().getTitle());
    }

    private ItemOrder getChangedItemOrder(final Order order, final ItemOrder itemOrder) {
        var paymentDay = itemOrder.getPaymentDay();
        var nextDelivery = paymentDay.plusDays(itemOrder.getPeriod());
        ItemOrder updatedItemOrder =
            itemOrderService.updateDeliveryInfo(paymentDay, nextDelivery, itemOrder);
        log.info("{}", updatedItemOrder.getPaymentDay());
        extendPeriod(order, updatedItemOrder);

        paymentDay.plusDays(itemOrder.getPeriod());
        return updatedItemOrder;
    }

    private boolean isPaymentDirectly(Order order, Integer period, ItemOrder itemOrder) {
        boolean noMargin = itemOrder.getPaymentDay()
            .plusDays(period)
            .isBefore(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))); //바궈야진

        if (noMargin) {
            log.info("directly pay");
            resetSchedule(order, itemOrder);
            return true;
        }
        return false;
    }

    private void deleteSchedule(Order order, ItemOrder itemOrder) {
        log.info("delete schedule");
        User user = getUser(order.getOrderId());
        deleteSchedule(itemOrder, user);
    }

    private void deleteSchedule(final ItemOrder itemOrder, final User user) {
            try {
                scheduler.deleteJob(jobKey(
                    user.getUserId() + itemOrder.getItem().getTitle(),
                    String.valueOf(user.getUserId()))
                );
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
    }

    private void extendPeriod(Order order, ItemOrder itemOrder) {
        log.warn("extendPeriod = {}", itemOrder.getPeriod());
        resetSchedule(order, itemOrder);
    }

    private void resetSchedule(Order order, ItemOrder itemOrder) {
        deleteSchedule(order, itemOrder);
        startSchedule(order, itemOrder);
    }

    private ItemOrder getItemOrder(Long itemOrderId) {
        return itemOrderService.findItemOrder(itemOrderId);
    }

    public User getUser(Long orderId) {
        Order order = orderService.findOrder(orderId);
        return order.getUser();
    }

    private ItemOrder findItemOrderInOrder(Order order, Long itemOrderId) {
        ItemOrder itemOrder = getItemOrder(itemOrderId);
        int i = order.getItemOrders().indexOf(itemOrder);
        return order.getItemOrders().get(i);
    }


    private void applySchedule(Order order, ItemOrder itemOrder) {
        User user = order.getUser();
        log.info("{} {}", order.getOrderId(), itemOrder.getItemOrderId());
        JobKey jobkey = jobKey(
            user.getUserId() + itemOrder.getItem().getTitle(),
            String.valueOf(user.getUserId())
        );

        JobDetail payDay = jobDetailService.build(jobkey, order.getOrderId(), itemOrder);
        Trigger lastTrigger = trigger.build(jobkey, itemOrder);

        schedule(payDay, lastTrigger);
    }

    private void schedule(JobDetail jobDetail, Trigger lastTrigger) {
        try {
            ListenerManager listenerManager = scheduler.getListenerManager();
            listenerManager.addJobListener(
                new JobListeners(trigger, itemOrderService, orderService, jobDetailService)
            );
            listenerManager.addTriggerListener(new TriggerListeners());
            scheduler.scheduleJob(jobDetail, lastTrigger);
        } catch (SchedulerException e) {
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.setRefireImmediately(true);
        }
    }
}
