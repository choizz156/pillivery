package com.team33.modulebatch.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;

import com.team33.modulebatch.BatchApiTest;
import com.team33.modulebatch.OrderVO;
import com.team33.modulebatch.listener.ItemSkipListener;

class PaymentItemReaderSkipTest extends BatchApiTest {

	private static final int CHUNK_SIZE = 1;
	private static final int SKIP_LIMIT = 1;
	private static final LocalDate NOW = LocalDate.now();

	@MockBean
	private ItemSkipListener skipListener;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@MockBean
	private ItemReader<OrderVO> testItemReader;

	@MockBean
	private ItemWriter<OrderVO> testItemWriter;

	private Step step;

	@DisplayName("itemreader에 에러 발생 시 skip할 수 있다.")
	@Test
	void test1() throws Exception {
		//then
		testStep();

		JobExecution jobExecution = jobRepository.createJobExecution("testJob", new JobParameters());
		StepExecution stepExecution = jobExecution.createStepExecution("paymentJobStep");
		jobRepository.add(stepExecution);

		when(testItemReader.read())
			.thenThrow(new DataAccessException("Simulated DB error") {
			})
			.thenReturn(new OrderVO(2L, true, Date.valueOf(NOW)))
			.thenReturn(null);

		//when
		step.execute(stepExecution);

		//then
		assertThat(stepExecution.getReadSkipCount()).isEqualTo(1);
		assertThat(stepExecution.getReadCount()).isEqualTo(1);
		verify(skipListener, times(1)).onSkipInRead(any(DataAccessException.class));
	}

	@DisplayName("skip limit를 넘어가면 step 예외를 던진다.")
	@Test
	void test2() throws Exception {
		//then
		testStep();

		JobExecution jobExecution = jobRepository.createJobExecution("testJob", new JobParameters());
		StepExecution stepExecution = jobExecution.createStepExecution("paymentJobStep");
		jobRepository.add(stepExecution);

		when(testItemReader.read())
			.thenThrow(new DataAccessException("Simulated DB error1") {
			})
			.thenReturn(new OrderVO(2L, true, Date.valueOf(NOW)))
			.thenThrow(new DataAccessException("Simulated DB error2") {
			})
			.thenReturn(null);

		//when
		step.execute(stepExecution);

		//then
		Throwable exception = stepExecution.getFailureExceptions()
			.stream()
			.findFirst()
			.orElse(null);

		assertThat(exception).isInstanceOf(SkipLimitExceededException.class);
		assertThat(stepExecution.getReadSkipCount()).isEqualTo(1);
		assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.FAILED);

		verify(skipListener, times(1)).onSkipInRead(any(DataAccessException.class));
	}

	private void testStep() {
		step = stepBuilderFactory.get("paymentJobStep")
			.<OrderVO, OrderVO>chunk(CHUNK_SIZE)
			.reader(testItemReader)
			.writer(testItemWriter)
			.faultTolerant()
			.skipLimit(SKIP_LIMIT)
			.skip(DataAccessException.class)
			.listener(skipListener)
			.build();
	}

}

