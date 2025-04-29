package com.team33.modulebatch.scheduler.job;

import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class PaymentScheduleJob extends QuartzJobBean {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private final Job paymentJob;
	private final JobLauncher jobLauncher;

	@Override
	protected void executeInternal(JobExecutionContext context) {

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

		LOGGER.info("start payment job, date = {}", now.toLocalDate());

		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("jobId", UUID.randomUUID().getMostSignificantBits())
				.addDate("paymentDate", Date.valueOf(now.toLocalDate()))
				.addLong("timestamp", now.toEpochSecond())
				.toJobParameters();

		LOGGER.info("jobParameters = {}", jobParameters);

		try {
			jobLauncher.run(paymentJob, jobParameters);
		} catch (JobExecutionAlreadyRunningException
				| JobRestartException
				| JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			throw new RuntimeException(e);
		}
	}
}
