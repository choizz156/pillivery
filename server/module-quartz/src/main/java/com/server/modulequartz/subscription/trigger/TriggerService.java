package com.server.modulequartz.subscription.trigger;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;
import team33.modulecore.domain.order.entity.ItemOrder;

@Slf4j
@Component
public class TriggerService {

    public Trigger build(JobKey jobKey, ItemOrder itemOrder) {

        return newTrigger()
            .forJob(jobKey)
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(itemOrder.getPeriod())
                .repeatForever()
            )
            .startAt(Date.from(itemOrder.getNextDelivery().toInstant()))
            .build();
    }
}
