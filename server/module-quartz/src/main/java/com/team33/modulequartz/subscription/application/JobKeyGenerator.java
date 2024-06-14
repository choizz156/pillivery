package com.team33.modulequartz.subscription.application;

import static org.quartz.JobKey.*;

import org.quartz.JobKey;

public class JobKeyGenerator {
	public static JobKey build(long orderId, String productName) {
		return 	jobKey(
			orderId + "-" + productName,
			String.valueOf(orderId)
		);
	}
}
