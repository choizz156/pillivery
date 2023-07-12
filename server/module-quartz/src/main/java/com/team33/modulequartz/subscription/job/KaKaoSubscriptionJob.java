package com.team33.modulequartz.subscription.job;


import static org.quartz.JobKey.jobKey;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.team33.modulequartz.subscription.service.SubscriptionService;
import com.team33.modulecore.domain.order.entity.ItemOrder;
import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.order.service.ItemOrderService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.user.entity.User;

/**
 Job 클래스내에서 @Autowired 할 수 있도록 하기 위해서 사용함
 */
@Slf4j
@RequiredArgsConstructor
@Component
@DisallowConcurrentExecution
public class KaKaoSubscriptionJob implements Job {

    private final ItemOrderService itemOrderService;
    private final OrderService orderService;
    private final Scheduler scheduler;
    private final JobDetailService jobDetail;
    private final SubscriptionService subscriptionService;

    @Override
    public void execute( JobExecutionContext context ) {

        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();

        ItemOrder itemOrder = (ItemOrder) mergedJobDataMap.get("itemOrder");
        log.info("start itemOrderId = {}", itemOrder.getItemOrderId());
        log.info("itemOrder title = {}", itemOrder.getItem().getTitle());

        Long orderId = (Long) mergedJobDataMap.get("orderId");
        log.info("start orderId = {}", orderId);

        updateDeliveryDate(itemOrder, orderId);

        Order newOrder = getNewOrder(itemOrder, orderId);

        connectKaKaoPay(newOrder.getOrderId());
    }

    private Order getNewOrder(final ItemOrder itemOrder, final Long orderId) {
        Order newOrder = getNewOrder(orderId);
        ItemOrder newItemOrder = itemOrderService.itemOrderCopy(orderId, newOrder, itemOrder);
        JobDetail newJob = updateJobDetails(itemOrder, orderId, newOrder, newItemOrder);

        updateJob(newJob);
        return newOrder;
    }

    private void updateJob(final JobDetail newJob) {
        try{
            scheduler.addJob(newJob, true);
        } catch(SchedulerException e){
            log.warn(e.getMessage());
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.refireImmediately();
        }
    }

    private void updateDeliveryDate(final ItemOrder itemOrder, final Long orderId) {
        ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("payment = {}", paymentDay);
        ZonedDateTime nextDelivery = paymentDay.plusDays(itemOrder.getPeriod());
        itemOrderService.updateDeliveryInfo(paymentDay, nextDelivery, itemOrder);
    }

    private Order getNewOrder( Long orderId ){
        orderService.completeOrder(orderId);
        Order order = orderService.findOrder(orderId);
        return orderService.deepCopy(order);
    }

    private JobDetail updateJobDetails( ItemOrder itemOrder, Long orderId, Order newOrder, ItemOrder newItemOrder ){
        User user = subscriptionService.getUser(orderId);
        JobKey jobkey = jobKey(user.getUserId() + itemOrder.getItem().getTitle(), String.valueOf(user.getUserId()));
        return jobDetail.build(jobkey, newOrder.getOrderId(), newItemOrder);
    }

    private void connectKaKaoPay( Long orderId ){

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("orderId", String.valueOf(orderId));

        URI uri = UriComponentsBuilder.newInstance().scheme("http").host("ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com").port(8080) // 호스트랑 포트는 나중에 변경해야한다.
                .path("/payments/kakao/subscription").queryParams(parameters).build().toUri();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(uri, String.class);
    }

}
