package com.team33.modulebatch.scheduler.job;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.team33.modulebatch.scheduler.application.JobDetailService;
import com.team33.modulebatch.scheduler.application.TriggerService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentJobRunner implements JobRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final Scheduler scheduler;
	private final JobDetailService jobDetailService;
	private final TriggerService triggerService;

	@Override
	public void run(String... args) throws Exception {

		JobDetail paymentScheduleJobDetails = jobDetailService.buildJobDetail(PaymentScheduleJob.class);
		try {
			scheduler.scheduleJob(paymentScheduleJobDetails, triggerService.now());
		} catch (SchedulerException e) {
			LOGGER.error("Error while scheduling job", e);
		}
	}
}
