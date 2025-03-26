package com.team33.modulebatch.step;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.backoff.FixedBackOffPolicy;

import com.team33.modulebatch.BatchApiTest;
import com.team33.modulebatch.OrderVO;
import com.team33.modulebatch.listener.ItemSkipListener;
import com.team33.moduleexternalapi.exception.PaymentApiException;

class PaymentRetryTest extends BatchApiTest {

	private static final int CHUNK_SIZE = 1;
	private static final int RETRY_LIMIT = 3;

	@MockBean
	private ItemSkipListener itemSkipListener;

	@MockBean
	private ItemReader<OrderVO> testItemReader;

	@MockBean
	private ItemWriter<OrderVO> testItemWriter;

	private Step step;

	private void testStep() {
		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(0L);

		step = stepBuilderFactory.get("paymentJobStep")
			.<OrderVO, OrderVO>chunk(CHUNK_SIZE)
			.reader(testItemReader)
			.writer(testItemWriter)
			.faultTolerant()
			.retryLimit(RETRY_LIMIT)
			.retry(PaymentApiException.class)
			.backOffPolicy(backOffPolicy)
			.listener(itemSkipListener)
			.build();
	}

	@DisplayName("item writer에서 에러 발생시 retry limit만큼 재시도한다.")
	@Test
	void test1() throws Exception {
		// given
		testStep();
		OrderVO order = new OrderVO();
		when(testItemReader.read()).thenReturn(order, (OrderVO)null);

		doThrow(new PaymentApiException("test exception 1"))
			.doThrow(new PaymentApiException("test exception 2"))
			.doNothing() //이것도 try한거임.
			.when(testItemWriter).write(anyList());

		JobExecution jobExecution = jobRepository.createJobExecution("testJob", new JobParameters());
		StepExecution stepExecution = jobExecution.createStepExecution("paymentJobStep");
		jobRepository.add(stepExecution);

		// When
		step.execute(stepExecution);

		// then
		assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
		verify(testItemWriter, times(RETRY_LIMIT)).write(anyList());
	}

	@DisplayName("item writer에서 에러가 retry limit이상으로 발생하면 예외를 던진다.")
	@Test
	void test2() throws Exception {
		//given
		testStep();
		OrderVO order = new OrderVO();
		when(testItemReader.read()).thenReturn(order, (OrderVO)null);

		doThrow(new PaymentApiException("test exception 1"))
			.when(testItemWriter).write(anyList());

		JobExecution jobExecution = jobRepository.createJobExecution("testJob", new JobParameters());
		StepExecution stepExecution = jobExecution.createStepExecution("paymentJobStep");
		jobRepository.add(stepExecution);

		// When
		step.execute(stepExecution);

		//then
		Throwable exception = stepExecution.getFailureExceptions()
			.stream()
			.findFirst()
			.orElse(null);
		assertThat(exception).isInstanceOf(ExhaustedRetryException.class);
		assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
		verify(testItemWriter, times(RETRY_LIMIT)).write(anyList());
	}

}
