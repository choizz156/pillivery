package com.team33.moduleapi.controller.quartz.jobtest;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.quartz.impl.matchers.GroupMatcher;

@Slf4j
public class TestTriggerListener implements TriggerListener{

    @Override
    public String getName() {
        return "테스트 트리거 리스터";
    }

    @Override
    public void triggerFired(final Trigger trigger, final JobExecutionContext context) {
        log.info("trigger 실행");
    }

    @Override
    public boolean vetoJobExecution(final Trigger trigger, final JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(final Trigger trigger) {
        log.info("트리거 오류");
    }

    @Override
    public void triggerComplete(final Trigger trigger, final JobExecutionContext context,
        final CompletedExecutionInstruction triggerInstructionCode) {
        log.info("트리거 수행");
    }
}
