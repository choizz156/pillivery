package com.team33.modulebatch.step;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;

import com.team33.modulebatch.BatchApiTest;
import com.team33.modulebatch.exception.ClientPaymentException;
import com.team33.modulebatch.listener.ItemSkipListener;


class PaymentItemWriterSkipTest extends BatchApiTest {

	private static final int CHUNK_SIZE = 1;
	private static final LocalDate NOW = LocalDate.now();
	private static final int SKIP_LIMIT = 1;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@MockBean
	private ItemSkipListener itemSkipListener;

	private ItemReader<SubscriptionOrderVO> testItemReader;
	private ItemWriter<SubscriptionOrderVO> testItemWriter;

	private Step step;
	private List<SubscriptionOrderVO> orders;

	@BeforeEach
	void setUpEach() {

		orders = List.of(
			new SubscriptionOrderVO(1L, true, NOW),
			new SubscriptionOrderVO(2L, true, NOW),
			new SubscriptionOrderVO(3L, true, NOW)
		);
	}

	@DisplayName("skip을 할 수 있다.")
	@Test
	void test1() throws Exception {

		setUpData(orders, List.of(2L));
		testStep();

		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("timestamp", System.currentTimeMillis())
			.toJobParameters();

		JobExecution jobExecution = jobRepository.createJobExecution("testJob", jobParameters);
		StepExecution stepExecution = jobExecution.createStepExecution("paymentJobStep");
		jobRepository.add(stepExecution);

		//when
		step.execute(stepExecution);

		//then
		verify(itemSkipListener).onSkipInWrite(
			argThat(item -> (item.getSubscriptionOrderId().equals(2L))),
			any(ClientPaymentException.class)
		);

		assertThat(jobExecution.getStepExecutions().iterator().next().getSkipCount()).isEqualTo(1);
		assertThat(jobExecution.getStepExecutions().iterator().next().getWriteCount()).isEqualTo(2);
	}

	@DisplayName("skipLimit를 초과하면 예외를 던진다.")
	@Test
	void test2() throws Exception {
		// Given
		setUpData(orders, List.of(2L, 3L));
		testStep();

		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("timestamp", System.currentTimeMillis())
			.toJobParameters();

		JobExecution jobExecution = jobRepository.createJobExecution("testJob", jobParameters);
		StepExecution stepExecution = jobExecution.createStepExecution("paymentJobStep");
		jobRepository.add(stepExecution);

		// When
		step.execute(stepExecution);

		Throwable exception = stepExecution.getFailureExceptions()
			.stream()
			.findFirst()
			.orElse(null);

		//then
		verify(itemSkipListener, times(1)).onSkipInWrite(
			argThat(item -> item.getSubscriptionOrderId() == 2L),
			any(ClientPaymentException.class)
		);

		verify(itemSkipListener, never()).onSkipInWrite(
			argThat(item -> item.getSubscriptionOrderId() == 3L),
			any(ClientPaymentException.class)
		);

		assertThat(exception).isInstanceOf(SkipLimitExceededException.class);
		assertThat(stepExecution.getSkipCount()).isEqualTo(1);
		assertThat(stepExecution.getWriteCount()).isEqualTo(1);
		assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
	}

	private void testStep() {

		step = stepBuilderFactory.get("paymentJobStep")
			.<SubscriptionOrderVO, SubscriptionOrderVO>chunk(CHUNK_SIZE)
			.reader(testItemReader)
			.writer(testItemWriter)
			.faultTolerant()
			.skipLimit(SKIP_LIMIT)
			.skip(ClientPaymentException.class)
			.skip(DataAccessException.class)
			.listener(itemSkipListener)
			.build();
	}

	private void setUpData(List<SubscriptionOrderVO> orders, List<Long> errorIds) {

		testItemReader = new IteratorItemReader<>(orders);

		testItemWriter = items -> {
			for (SubscriptionOrderVO item : items) {
				if (errorIds.contains(item.getSubscriptionOrderId())) {
					throw new ClientPaymentException("Payment API error");
				}
			}
		};
	}
}

