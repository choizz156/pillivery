package com.team33.modulequartz.subscription.infra;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TriggerListeners implements TriggerListener {

	private static final String PAYMENTS_TRIGGER = "payments Trigger";

	@Override
	public String getName() {
		return PAYMENTS_TRIGGER;
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		log.info("실행된 트리거 => {}", trigger.getKey());
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return false;
	}

	@Override
	public void triggerMisfired(Trigger trigger) {
		log.info("중단된 트리거 => {}", trigger.getKey());
	}

	@Override
	public void triggerComplete(
		Trigger trigger,
		JobExecutionContext context,
		CompletedExecutionInstruction triggerInstructionCode
	) {
		log.info("완료된 트리거 => {}", trigger.getKey());
	}
}
