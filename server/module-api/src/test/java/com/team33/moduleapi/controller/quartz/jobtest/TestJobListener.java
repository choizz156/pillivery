package com.team33.moduleapi.controller.quartz.jobtest;

import static org.quartz.DateBuilder.IntervalUnit.SECOND;
import static org.quartz.DateBuilder.futureDate;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import com.team33.moduleapi.exception.BusinessLogicException;
import com.team33.moduleapi.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

@Slf4j
@RequiredArgsConstructor
@PersistJobDataAfterExecution
public class TestJobListener implements JobListener {


    @Override
    public String getName() {
        return "test job listener";
    }

    @Override
    public void jobToBeExecuted(final JobExecutionContext context) {
        log.info("job 실행전");
    }

    @Override
    public void jobExecutionVetoed(final JobExecutionContext context) {
    }

    @Override
    public void jobWasExecuted(final JobExecutionContext context,
        final JobExecutionException jobException) {
        log.info("job 실행 후");
        JobKey key = context.getJobDetail().getKey();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        int retryCount = (int) jobDataMap.get("retry");
        log.info("retryCount = {}", retryCount);
        log.info("실행된 job의 jobkey = {}", key);

        retryOrDeleteIfJobException(context, jobException, key, jobDataMap, retryCount);
    }

    private void retryOrDeleteIfJobException(
        final JobExecutionContext context,
        final JobExecutionException jobException,
        final JobKey key,
        final JobDataMap jobDataMap,
        final int retryCount
    ) {
        if (jobException != null) {
            log.warn("job exception = {}", jobException.getMessage());
            retryImmediately(jobException, jobDataMap, retryCount);
            retryIn3Hours(context, jobDataMap, retryCount);
            deleteJob(context, jobException, key, retryCount);
        }
    }

    private void retryIn3Hours(
        final JobExecutionContext context,
        final JobDataMap jobDataMap,
        int retryCount
    ) {
        if (retryCount == 1) {
            log.warn("두번째 시도");
            jobDataMap.put("retry", ++retryCount);
            Trigger trigger = newTrigger()
                .startAt(futureDate(3, SECOND))
                .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(1)
                    .withRepeatCount(7)
                )
                .withIdentity(new TriggerKey("test2", "test2"))
                .build();

            reschedule(context, trigger);
        }
    }

    private void reschedule(final JobExecutionContext context, final Trigger trigger) {
        try {
            log.warn("재시도 스케쥴 설정");
            log.warn("{}",
                context.getScheduler().getTrigger(TriggerKey.triggerKey("test1", "test1")));
            context.getScheduler().rescheduleJob(TriggerKey.triggerKey("test1", "test1"), trigger);
            log.warn("{}",
                context.getScheduler().getTrigger(TriggerKey.triggerKey("test1", "test1")));
            log.warn("설정 완료");
            log.warn("{}", context.getScheduler().getTrigger(trigger.getKey()));
        } catch (SchedulerException e) {
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.setRefireImmediately(true);
        }
    }

    private void retryImmediately(
        final JobExecutionException jobException,
        final JobDataMap jobDataMap,
        int retryCount
    ) {
        if (retryCount == 0) {
            log.warn("즉시 재시도");
            jobDataMap.put("retry", ++retryCount);
            jobException.setRefireImmediately(true);
        }
    }

    private void deleteJob(
        final JobExecutionContext context,
        final JobExecutionException jobException,
        final JobKey key,
        final int retryCount
    ) {
        if (retryCount == 2) {
            log.error("스케쥴링 취소 = {}", key);
            jobException.setUnscheduleAllTriggers(true);
            deleteSchedule(context, key);
        }
    }

    private void deleteSchedule(final JobExecutionContext context, final JobKey key) {
        try {
            context.getScheduler().deleteJob(key);
            throw new BusinessLogicException(ExceptionCode.SCHEDULE_CANCEL);
        } catch (SchedulerException e) {
            JobExecutionException jobExecutionException = new JobExecutionException(e);
            jobExecutionException.setRefireImmediately(true);
        }
    }
}
