package com.team33.modulebatch.scheduler.application;

import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TriggerService {

	public Trigger now() {
		return TriggerBuilder.newTrigger().startNow().build();
	}
}
