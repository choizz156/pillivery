package com.team33.modulequartz.subscription.job;

import static org.quartz.JobBuilder.newJob;

import com.team33.modulecore.domain.order.entity.OrderItem;
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
        log.warn("job datail itemOrderId = {}", orderItem.getItemOrderId());

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
