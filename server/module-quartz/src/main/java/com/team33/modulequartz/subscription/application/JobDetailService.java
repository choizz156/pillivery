package com.team33.modulequartz.subscription.application;

import static org.quartz.JobBuilder.*;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

import com.team33.modulequartz.subscription.domain.KaKaoSubscriptionJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobDetailService {

	public JobDetail build(JobKey jobKey, long orderId, long orderItemId) {

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("orderId", orderId);
		jobDataMap.put("orderItemId", orderItemId);
		jobDataMap.put("retry", 0);

		return newJob(KaKaoSubscriptionJob.class)
			.withIdentity(jobKey.getName(), jobKey.getGroup())
			.storeDurably(true)
			.usingJobData(jobDataMap)
			.build();
	}
}
