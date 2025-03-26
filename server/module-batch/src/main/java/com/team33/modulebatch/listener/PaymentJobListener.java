package com.team33.modulebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class PaymentJobListener implements JobExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	@Override
	public void beforeJob(JobExecution jobExecution) {
		Long jobId = jobExecution.getJobInstance().getId();
		jobExecution.getExecutionContext().putLong("jobId", jobId);
		LOGGER.info("start Job ::: JobId = {}, date = {}",
			jobId,
			jobExecution.getJobParameters().getDate("paymentDate")
		);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		LOGGER.info("finish Job ::: JobId = {}, date = {}, status = {}",
			jobExecution.getJobId(),
			jobExecution.getJobParameters().getDate("paymentDate"),
			jobExecution.getStatus()
		);
	}
}
