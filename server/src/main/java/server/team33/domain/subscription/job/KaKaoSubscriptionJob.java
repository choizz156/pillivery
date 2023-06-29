package server.team33.domain.subscription.job;


import static org.quartz.JobKey.jobKey;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import server.team33.domain.order.entity.ItemOrder;
import server.team33.domain.order.entity.Order;
import server.team33.domain.order.service.ItemOrderService;
import server.team33.domain.order.service.OrderService;
import server.team33.domain.subscription.service.SubscriptionService;
import server.team33.domain.user.entity.User;

@Slf4j
@RequiredArgsConstructor
@Component

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

        ZonedDateTime paymentDay = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("payment = {}", paymentDay);

        Long orderId = (Long) mergedJobDataMap.get("orderId");
        log.info("start orderId = {}", orderId);
        ZonedDateTime nextDelivery = paymentDay.plusDays(itemOrder.getPeriod());
        itemOrderService.updateDeliveryInfo(orderId, paymentDay, nextDelivery, itemOrder);

        Order newOrder = getNewOrder(orderId);
        log.info("newOrder Id = {}", newOrder.getOrderId());
        ItemOrder newItemOrder = itemOrderService.itemOrderCopy(orderId, newOrder, itemOrder);

        JobDetail newJob = updateJob(itemOrder, orderId, newOrder, newItemOrder);
        try{
            scheduler.addJob(newJob, true);
        } catch(SchedulerException e){
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.refireImmediately();
        }
        connectKaKaoPay(newOrder.getOrderId());
    }

    private Order getNewOrder( Long orderId ){
        orderService.completeOrder(orderId);
        Order order = orderService.findOrder(orderId);
        return orderService.deepCopy(order);
    }

    private JobDetail updateJob( ItemOrder itemOrder, Long orderId, Order newOrder, ItemOrder newItemOrder ){
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
