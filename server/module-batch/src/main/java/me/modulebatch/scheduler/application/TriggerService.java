package me.modulebatch.scheduler.application;

import static org.quartz.CalendarIntervalScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;

import java.util.Date;

import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.domain.entity.OrderItem;

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
			.startAt(Date.from(orderItem.getNextPaymentDay().toInstant()))
			.build();
	}

	public Trigger now() {
		return TriggerBuilder.newTrigger().startNow().build();
	}
}
