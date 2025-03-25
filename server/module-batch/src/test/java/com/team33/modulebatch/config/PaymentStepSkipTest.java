package com.team33.modulebatch.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulebatch.OrderVO;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulebatch.listener.ItemSkipListener;
import com.team33.modulecore.config.redis.EmbededRedisConfig;
import com.team33.moduleexternalapi.exception.PaymentApiException;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

@SpringBootTest(classes = {
	BatchTestConfig.class,
	PaymentJobConfig.class,
	PaymentApiDispatcher.class,
	EmbededRedisConfig.class
})
@SpringBatchTest
@EnableAutoConfiguration
@EnableBatchProcessing
@ActiveProfiles("test")
class PaymentStepSkipTest {

	private static final int CHUNK_SIZE = 1;
	private static final int SKIP_LIMIT = 1;
	private static final int RETRY_LIMIT = 3;
	private static final LocalDate NOW = LocalDate.now();

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobRepository jobRepository;

	@MockBean
	private PaymentApiDispatcher paymentApiDispatcher;

	@MockBean
	private RestTemplateSender restTemplateSender;

	@MockBean
	private ItemSkipListener itemSkipListener;

	private ItemReader<OrderVO> testItemReader;
	private ItemWriter<OrderVO> testItemWriter;
	private Step step;

	@DisplayName("skip을 할 수 있다.")
	@Test
	void test1() throws Exception {

		List<OrderVO> orders = List.of(
			new OrderVO(1L, true, Date.valueOf(NOW)),
			new OrderVO(2L, true, Date.valueOf(NOW)),
			new OrderVO(3L, true, Date.valueOf(NOW))
		);
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
			argThat(item -> (item.getOrderId().equals(2L))),
			any(PaymentApiException.class)
		);

		assertThat(jobExecution.getStepExecutions().iterator().next().getSkipCount()).isEqualTo(1);
		assertThat(jobExecution.getStepExecutions().iterator().next().getWriteCount()).isEqualTo(2);
	}

	@DisplayName("skipLimit를 초과하면 실패한다.")
	@Test
	void test2() throws Exception {
		// Given
		List<OrderVO> orders = List.of(
			new OrderVO(1L, true, Date.valueOf(NOW)),
			new OrderVO(2L, true, Date.valueOf(NOW)),
			new OrderVO(3L, true, Date.valueOf(NOW))
		);
		setUpData(orders, List.of(2L, 3L));
		testStep();

		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("timestamp", System.currentTimeMillis())
			.toJobParameters();

		JobExecution jobExecution = jobRepository.createJobExecution("testJob", jobParameters);
		StepExecution stepExecution = jobExecution.createStepExecution("paymentJobStep");
		jobRepository.add(stepExecution);

		// When
		step.execute(stepExecution); // 예외를 직접 던지지 않을 수 있음
		Throwable exception = stepExecution.getFailureExceptions()
			.stream()
			.findFirst()
			.orElse(null);

		//then
		verify(itemSkipListener, times(1)).onSkipInWrite(
			argThat(item -> item.getOrderId() == 2L),
			any(PaymentApiException.class)
		);

		verify(itemSkipListener, never()).onSkipInWrite(
			argThat(item -> item.getOrderId() == 3L),
			any(PaymentApiException.class)
		);

		assertThat(exception).isInstanceOf(SkipLimitExceededException.class);
		assertThat(stepExecution.getSkipCount()).isEqualTo(1);
		assertThat(stepExecution.getWriteCount()).isEqualTo(1);
		assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
	}

	//TODO: item reader도 skiptest

	private void testStep() {
		step = stepBuilderFactory.get("paymentJobStep")
			.<OrderVO, OrderVO>chunk(CHUNK_SIZE)
			.reader(testItemReader)
			.writer(testItemWriter)
			.faultTolerant()
			.skipLimit(SKIP_LIMIT)
			.skip(PaymentApiException.class)
			.skip(DataAccessException.class)
			.retryLimit(RETRY_LIMIT)
			.retry(PaymentApiException.class)
			.listener(itemSkipListener)
			.build();
	}

	private void setUpData(List<OrderVO> orders, List<Long> errorIds) {
		testItemReader = new IteratorItemReader<>(orders);

		testItemWriter = items -> {
			for (OrderVO item : items) {
				if (errorIds.contains(item.getOrderId())) {
					throw new PaymentApiException("Payment API error");
				}
			}
		};
	}
}

