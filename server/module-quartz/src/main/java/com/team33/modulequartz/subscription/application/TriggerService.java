package com.team33.modulequartz.subscription.application;

import static org.quartz.CalendarIntervalScheduleBuilder.*;
import static org.quartz.DateBuilder.IntervalUnit.*;
import static org.quartz.DateBuilder.*;
import static org.quartz.TriggerBuilder.*;

import java.util.Date;

import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

import com.team33.modulecore.order.domain.OrderItem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TriggerService {

	public Trigger build(JobKey jobKey, OrderItem orderItem) {

		return newTrigger()
			.forJob(jobKey)
			.withSchedule(
				calendarIntervalSchedule().withIntervalInDays(orderItem.getPeriod())
			)
			.withIdentity(new TriggerKey(jobKey.getName(), jobKey.getGroup()))
			.startAt(Date.from(orderItem.getNextDelivery().toInstant()))
			.build();
	}

	public Trigger retryTrigger() {
		return newTrigger()
			.startAt(futureDate(1, HOUR))
			.build();
	}
}
