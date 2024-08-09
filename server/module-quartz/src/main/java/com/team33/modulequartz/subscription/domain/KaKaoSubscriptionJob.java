package com.team33.modulequartz.subscription.domain;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.moduleexternalapi.infra.WebClientSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class KaKaoSubscriptionJob implements Job {

	private final WebClientSender webClientSender;

	@Override
	public void execute(JobExecutionContext context) {

		JobDataMap mergedJobDataMap = context.getMergedJobDataMap();

		Long orderId = mergedJobDataMap.getLong("orderId");

		checkOrder(orderId);

		send(orderId);
	}

	private void send(long orderId) {
		try {
			webClientSender.sendToPost(
				null,
				"http://localhost:8080/payments/approve/subscriptions/" + orderId,
				null,
				String.class
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void checkOrder(Long orderId) {
		if (orderId == null) {
			throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
		}
	}
}
