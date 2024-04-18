package com.team33.modulequartz.subscription.application;

import static org.quartz.JobBuilder.newJob;

import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulequartz.subscription.domain.KaKaoSubscriptionJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobDetailService {

    public JobDetail build(JobKey jobKey, Long orderId, OrderItem orderItem) {

        log.warn("job detail orderId= {}", orderId);
        log.warn("job datail itemOrderId = {}", orderItem.getOrderItemId());

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("orderId", orderId);
        jobDataMap.put("itemOrder", orderItem);
        jobDataMap.put("retry", 0);

        return newJob(KaKaoSubscriptionJob.class)
            .withIdentity(jobKey.getName(), jobKey.getGroup())
            .storeDurably(true)
            .usingJobData(jobDataMap)
            .build();
    }
}
