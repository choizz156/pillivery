package server.team33.domain.subscription.service;

import static org.quartz.JobKey.jobKey;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;
import server.team33.domain.order.entity.ItemOrder;
import server.team33.domain.order.entity.Order;
import server.team33.domain.order.service.ItemOrderService;
import server.team33.domain.order.service.OrderService;
import server.team33.domain.subscription.job.JobDetailService;
import server.team33.domain.subscription.trigger.TriggerService;
import server.team33.domain.user.entity.User;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final Scheduler scheduler;
    private final TriggerService trigger;
    private final JobDetailService jobDetail;
    private final OrderService orderService;
    private final ItemOrderService itemOrderService;


    public void startSchedule(Order order, ItemOrder itemOrder) {
        applySchedule(order, itemOrder);
    }


    public ItemOrder changePeriod(Long orderId, Integer period, Long itemOrderId) {

        ItemOrder itemOrder = itemOrderService.setItemPeriod(orderId, period,
            findItemOrderInOrder(orderId, itemOrderId));

        if (payDirectly(orderId, period, itemOrder)) {
            itemOrder.getPaymentDay().plusDays(itemOrder.getPeriod());
            return itemOrder;
        }

        ZonedDateTime paymentDay = itemOrder.getPaymentDay();
        ZonedDateTime nextDelivery = paymentDay.plusDays(itemOrder.getPeriod());
        ItemOrder updatedItemOrder = itemOrderService.updateDeliveryInfo(orderId, paymentDay,
            nextDelivery, itemOrder);

        extendPeriod(orderId, updatedItemOrder);

        paymentDay.plusDays(itemOrder.getPeriod());
        return updatedItemOrder;
    }


    private boolean payDirectly(Long orderId, Integer period, ItemOrder itemOrder){
        boolean noMargin = itemOrder.getPaymentDay().plusDays(period)
            .isBefore(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))); //바궈야진
        log.info("margin = {}", noMargin);

        if (noMargin) {
            log.info("directly pay");
            resetSchedule(orderId, itemOrder);
            return true;
        }
        return false;
    }

    private void deleteSchedule(Long orderId, ItemOrder itemOrder){
        log.info("delete schedule");
        User user = getUser(orderId);
        try {
            scheduler.deleteJob(jobKey(user.getUserId() + itemOrder.getItem().getTitle(),
                String.valueOf(user.getUserId())));
        } catch (SchedulerException e) {
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.refireImmediately();
        }
    }

    private void extendPeriod(Long orderId, ItemOrder itemOrder) {
        log.warn("extendPeriod = {}", itemOrder.getPeriod());
        resetSchedule(orderId, itemOrder);
    }


    public ItemOrder delayDelivery(Long orderId, Integer delay, Long itemOrderId) {
        log.info("delay Delivery");
        ItemOrder itemOrder = itemOrderService.delayDelivery(orderId, delay,
            findItemOrderInOrder(orderId, itemOrderId));
        resetSchedule(orderId, itemOrder);
        return itemOrder;
    }

    public void cancelScheduler(Long orderId, Long itemOrderId) {
        log.info("cancelScheduler");
        ItemOrder itemOrder = getItemOrder(itemOrderId);

        deleteSchedule(orderId, itemOrder);
        itemOrderService.cancelItemOrder(orderId, itemOrder);
        log.warn("canceled item title = {}", itemOrder.getItem().getTitle());
    }

    private void resetSchedule(Long orderId, ItemOrder itemOrder){
        Order order = orderService.findOrder(orderId);
        deleteSchedule(orderId, itemOrder);
        startSchedule(order, itemOrder);
    }

    public ItemOrder getItemOrder(Long itemOrderId) {
        return itemOrderService.findItemOrder(itemOrderId);
    }

    public User getUser(Long orderId) {
        Order order = orderService.findOrder(orderId);
        return order.getUser();
    }

    private ItemOrder findItemOrderInOrder(Long orderId, Long itemOrderId) {
        Order order = orderService.findOrder(orderId);
        ItemOrder itemOrder = getItemOrder(itemOrderId);
        int i = order.getItemOrders().indexOf(itemOrder);
        return order.getItemOrders().get(i);
    }


    private void schedule(JobDetail payDay, Trigger lastTrigger) {
        try {
            scheduler.scheduleJob(payDay, lastTrigger);
        } catch (SchedulerException e) {
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.refireImmediately();
        }
    }

    private void applySchedule(Order order, ItemOrder itemOrder) {
        User user = order.getUser();
        JobKey jobkey = jobKey(
            user.getUserId() + itemOrder.getItem().getTitle(),
            String.valueOf(user.getUserId())
        );
        JobDetail payDay = jobDetail.build(jobkey, order.getOrderId(), itemOrder);
        Trigger lastTrigger = trigger.build(jobkey, order.getOrderId(), itemOrder);
        schedule(payDay, lastTrigger);
    }
}
