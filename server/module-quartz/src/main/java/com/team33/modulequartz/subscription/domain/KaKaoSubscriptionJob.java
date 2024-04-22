package com.team33.modulequartz.subscription.domain;


import com.team33.modulecore.orderitem.domain.OrderItem;
import com.team33.modulecore.exception.ExceptionCode;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class KaKaoSubscriptionJob implements Job {

    private final RestTemplate restTemplate;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();

        OrderItem orderItem = (OrderItem) mergedJobDataMap.get("itemOrder");
        log.info("start itemOrderId = {}", orderItem.getId());
        log.info("itemOrder title = {}", orderItem.getItem().getTitle());

        Long orderId = (Long) mergedJobDataMap.get("orderId");
        log.info("start orderId = {}", orderId);

        if (connectKaKaoPay(orderId) == null) {
            throw new JobExecutionException(ExceptionCode.PAYMENT_FAIL.getMessage());
        }
    }


    private String connectKaKaoPay(Long orderId) {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("orderId", String.valueOf(orderId));

        URI uri = UriComponentsBuilder.newInstance()
            .scheme("http").host("ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com")
            .port(8080)
            .path("/payments/kakao/subscription")
            .queryParams(parameters)
            .build().toUri();

        return restTemplate.postForObject(uri, null, String.class);
    }

}
