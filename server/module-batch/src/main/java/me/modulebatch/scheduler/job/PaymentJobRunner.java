package me.modulebatch.scheduler.job;

import java.util.HashMap;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import me.modulebatch.scheduler.application.JobDetailService;
import me.modulebatch.scheduler.application.TriggerService;

@RequiredArgsConstructor
@Component
public class PaymentJobRunner implements JobRunner {

	private final Scheduler scheduler;
	private final JobDetailService jobDetailService;
	private final TriggerService triggerService;

	@Override
	public void run(String... args) throws Exception {

		JobDetail jobDetail = jobDetailService.buildJobDetail(PaymentScheduleJob.class, new HashMap<>());

		try {
			scheduler.scheduleJob(jobDetail, triggerService.now());
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

}
