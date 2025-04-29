package com.team33.modulebatch.scheduler.job;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import com.team33.modulebatch.scheduler.application.JobDetailService;
import com.team33.modulebatch.scheduler.application.TriggerService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentJobRunner implements JobRunner {

	private final Scheduler scheduler;
	private final JobDetailService jobDetailService;
	private final TriggerService triggerService;

	@Override
	public void run(String... args) throws Exception {

		JobDetail jobDetail = jobDetailService.buildJobDetail(PaymentScheduleJob.class);

		try {
			scheduler.scheduleJob(jobDetail, triggerService.now());
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
}
