package com.team33.modulebatch.scheduler.application;

import static org.quartz.JobBuilder.*;

import java.time.ZonedDateTime;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobDetailService {

	public JobDetail buildJobDetail(Class<? extends Job> job) {

		JobDataMap jobDataMap = new JobDataMap();

		return newJob(job)
			.withIdentity("payment-Job-" + ZonedDateTime.now(), "batch")
			.usingJobData(jobDataMap)
			.storeDurably(true)
			.build();
	}
}
