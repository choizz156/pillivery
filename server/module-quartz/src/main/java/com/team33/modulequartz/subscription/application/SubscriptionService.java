package com.team33.modulequartz.subscription.application;

import static org.quartz.JobKey.jobKey;

import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.user.domain.User;
import com.team33.modulequartz.subscription.infra.JobListeners;
import com.team33.modulequartz.subscription.infra.TriggerListeners;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
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
    private final OrderItemService orderItemService;
    private final OrderQueryService orderQueryService;


    public void startSchedule(Order order, OrderItem orderItem) {
        applySchedule(order, orderItem);
        log.info("orderId = {}, itemOrderId ={} ==> 스케쥴 설정완료", order.getId());
    }

    @Transactional
    public OrderItem changePeriod(Long orderId, Integer period, Long itemOrderId) {

        Order order = orderQueryService.findOrder(orderId);
        OrderItem orderItem = findItemOrderInOrder(order, itemOrderId);

        orderItemService.setItemPeriod(period, orderItem);
        log.info("기존 아이템 결제 주기 ={}", orderItem.getPeriod());

        if (isPaymentDirectly(order, period, orderItem)) {
            orderItem.getPaymentDay().plusDays(orderItem.getPeriod());
            return orderItem;
        }
        return getChangedItemOrder(order, orderItem);
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
        Order order = orderQueryService.findOrder(orderId);
        OrderItem orderItem = getItemOrder(itemOrderId);

        deleteSchedule(order, orderItem);
        orderItemService.cancelItemOrder(orderId, orderItem);
        log.info("canceled item title = {}", orderItem.getItem().getTitle());
    }

    private OrderItem getChangedItemOrder(final Order order, final OrderItem orderItem) {
        var paymentDay = orderItem.getPaymentDay();
        var nextDelivery = paymentDay.plusDays(orderItem.getPeriod());
        OrderItem updatedOrderItem =
            orderItemService.updateDeliveryInfo(paymentDay, nextDelivery, orderItem);
        log.info("{}", updatedOrderItem.getPaymentDay());
        extendPeriod(order, updatedOrderItem);

        paymentDay.plusDays(orderItem.getPeriod());
        return updatedOrderItem;
    }

    private boolean isPaymentDirectly(Order order, Integer period, OrderItem orderItem) {
        boolean noMargin = orderItem.getPaymentDay()
            .plusDays(period)
            .isBefore(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));

        if (noMargin) {
            log.info("directly pay");
            resetSchedule(order, orderItem);
            return true;
        }
        return false;
    }

    private void deleteSchedule(Order order, OrderItem orderItem) {
        log.info("delete schedule");
        User user = getUser(order.getId());
        deleteSchedule(orderItem, user);
    }

    private void deleteSchedule(final OrderItem orderItem, final User user) {
        try {
            scheduler.deleteJob(jobKey(
                user.getId() + orderItem.getItem().getTitle(),
                String.valueOf(user.getId()))
            );
        } catch (SchedulerException e) {
            log.error(
                "스케쥴 삭제 실패 job => {},{}",
                JobKey.jobKey("user.getUserId() + itemOrder.getItem().getTitle()")
            );
        }
    }

    private void extendPeriod(Order order, OrderItem orderItem) {
        log.warn("extendPeriod = {}", orderItem.getPeriod());
        resetSchedule(order, orderItem);
    }

    private void resetSchedule(Order order, OrderItem orderItem) {
        deleteSchedule(order, orderItem);
        startSchedule(order, orderItem);
    }

    private OrderItem getItemOrder(Long itemOrderId) {
        return orderItemService.findOrderItem(itemOrderId);
    }

    public User getUser(Long orderId) {
        Order order = orderQueryService.findOrder(orderId);
        return order.getUser();
    }

    private OrderItem findItemOrderInOrder(Order order, Long itemOrderId) {
        OrderItem orderItem = getItemOrder(itemOrderId);
        int i = order.getOrderItems().indexOf(orderItem);
        return order.getOrderItems().get(i);
    }


    private void applySchedule(Order order, OrderItem orderItem) {
        User user = order.getUser();
        log.info("{} {}", order.getId(), orderItem.getId());
        JobKey jobkey = jobKey(
            user.getId() + orderItem.getItem().getTitle(),
            String.valueOf(user.getId())
        );

        JobDetail payDay = jobDetailService.build(jobkey, order.getId(), orderItem);
        Trigger lastTrigger = trigger.build(jobkey, orderItem);

        schedule(payDay, lastTrigger);
    }

    private void schedule(JobDetail jobDetail, Trigger lastTrigger) {
        try {
            ListenerManager listenerManager = scheduler.getListenerManager();
            listenerManager.addJobListener(
                new JobListeners(
                    trigger,
                    orderItemService,
                    orderService,
                    jobDetailService,
                    orderQueryService
                )
            );
            listenerManager.addTriggerListener(new TriggerListeners());
            scheduler.scheduleJob(jobDetail, lastTrigger);
        } catch (SchedulerException e) {
            log.error("스케쥴 등록 실패 job => {},{}", jobDetail.getKey().getName());
        }
    }
}
