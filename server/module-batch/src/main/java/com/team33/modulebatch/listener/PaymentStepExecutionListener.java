package com.team33.modulebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class PaymentStepExecutionListener implements StepExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (stepExecution.getStatus() == BatchStatus.FAILED) {
			Throwable exception = stepExecution.getFailureExceptions().get(0);
			LOGGER.error("Step 실패: {}, stepId: {}, jobId: {}",
					exception.getMessage(),
					stepExecution.getId(),
					stepExecution.getJobExecution().getId()
			);
		}
		return stepExecution.getExitStatus();
	}
}
