package com.team33.modulebatch.scheduler.application;

import java.util.Date;

import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TriggerService {

	public Trigger now() {
		Date startTime = new Date(System.currentTimeMillis() + 10_000);
		return TriggerBuilder.newTrigger().startAt(startTime).build();
	}
}
