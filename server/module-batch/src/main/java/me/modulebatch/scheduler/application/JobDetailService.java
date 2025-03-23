package me.modulebatch.scheduler.application;

import static org.quartz.JobBuilder.*;

import java.util.Map;

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

	public JobDetail buildJobDetail(Class<? extends Job> job, Map<String, Object> params) {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.putAll(params);

		return newJob(job)
			.withIdentity("payment-Job", "batch")
			.usingJobData(jobDataMap)
			.build();
	}
}
