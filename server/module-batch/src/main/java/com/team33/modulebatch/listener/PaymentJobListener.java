package com.team33.modulebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class PaymentJobListener implements JobExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	@Override
	public void beforeJob(JobExecution jobExecution) {

		LOGGER.info("start Job ::: jobExecution Id = {}, date = {}",
			jobExecution.getJobId(),
			jobExecution.getJobParameters().getDate("paymentDate")
		);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			LOGGER.info("Job Completed ::: jobExecution Id = {}, date = {}, status = {}",
				jobExecution.getJobId(),
				jobExecution.getJobParameters().getDate("paymentDate"),
				jobExecution.getStatus()
			);

			LOGGER.info("batch app exit");
			System.exit(0);
		}

		LOGGER.error("Job is not Completed ::: jobExecution Id = {}, date = {}, status = {}",
			jobExecution.getJobId(),
			jobExecution.getJobParameters().getDate("paymentDate"),
			jobExecution.getStatus()
		);
	}
}
