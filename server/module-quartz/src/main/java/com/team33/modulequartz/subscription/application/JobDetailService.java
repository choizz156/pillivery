package com.team33.modulequartz.subscription.application;

import static org.quartz.JobBuilder.*;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

import com.team33.modulecore.order.domain.entity.OrderItem;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulequartz.subscription.domain.KaKaoSubscriptionJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobDetailService {

	public JobDetail build(JobKey jobKey, long orderId, OrderItem orderItem) {

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("orderId", orderId);
		jobDataMap.put("orderItem", orderItem);
		jobDataMap.put("retry", 0);

		return newJob(KaKaoSubscriptionJob.class)
			.withIdentity(jobKey.getName(), jobKey.getGroup())
			.storeDurably(true)
			.usingJobData(jobDataMap)
			.build();
	}

	public JobDetail build(JobKey jobKey, long orderId) {

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("orderId", orderId);
		jobDataMap.put("retry", 0);

		return newJob(KaKaoSubscriptionJob.class)
			.withIdentity(jobKey.getName(), jobKey.getGroup())
			.storeDurably(true)
			.usingJobData(jobDataMap)
			.build();
	}

	public JobDetail build(JobKey jobKey, Order order) {

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("order", order);
		jobDataMap.put("retry", 0);

		return newJob(KaKaoSubscriptionJob.class)
			.withIdentity(jobKey.getName(), jobKey.getGroup())
			.storeDurably(true)
			.usingJobData(jobDataMap)
			.build();
	}
}
