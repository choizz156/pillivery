package com.team33.modulebatch.step;

import static org.assertj.core.api.Assertions.*;

import java.sql.Date;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.team33.modulebatch.BatchApiTest;
import com.team33.modulebatch.domain.DelayedItemRepository;
import com.team33.modulebatch.domain.entity.DelayedItem;

class RetryStepConfigTest extends BatchApiTest {

	private static final ZonedDateTime REQUEST_DATE = ZonedDateTime.now();

	@Autowired
	private DelayedItemRepository delayedItemRepository;

	@Autowired
	@Qualifier("delayedItemReader")
	private ItemReader<DelayedItem> delayedItemReader;

	@Autowired
	@Qualifier("delayedItemWriter")
	private ItemWriter<DelayedItem> delayedItemWriter;


	@BeforeEach
	void setUpEach() throws Exception {
		insertTestData();
	}


	@DisplayName("item reader가 작동한다. (총 개수 100개, 읽어올 아이템 20개)")
	@Test
	void test1() throws Exception {
		Date date = Date.valueOf(REQUEST_DATE.toLocalDate());
		JobParameters jobParameters = new JobParametersBuilder()
			.addDate("paymentDate", date)
			.toJobParameters();

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
		StepSynchronizationManager.register(stepExecution);

		int count = 0;
		while (delayedItemReader.read() != null) {
			count++;
		}

		assertThat(count).isEqualTo(100);
	}

	private void insertTestData() {

		for (long i = 1; i <= 100; i++) {
			DelayedItem delayedItem = DelayedItem.of(i, "test");
			delayedItemRepository.save(delayedItem);
		}
	}
	// @DisplayName("item writer가 작동하여 서버에 요청을 보낸다.(20개)")
	// @Test
	// void test2() throws Exception {
	// 	// given
	// 	Date date = Date.valueOf(REQUEST_DATE.toLocalDate());
	// 	JobParameters jobParameters = new JobParametersBuilder()
	// 		.addDate("paymentDate", date)
	// 		.toJobParameters();
	//
	// 	StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
	// 	StepSynchronizationManager.register(stepExecution);
	//
	// 	SubscriptionOrderVO order;
	// 	var list = new ArrayList<SubscriptionOrderVO>();
	// 	while ((order = itemReader.read()) != null) {
	// 		list.add(order);
	// 	}
	//
	// 	list.forEach(subscriptionOrderVO -> {
	// 		paymentItemProcessor.initialize(1L);
	// 		paymentItemProcessor.process(subscriptionOrderVO);
	// 	});
	//
	// 	// when
	// 	itemWriter.write(list);
	//
	// 	// then
	// 	verify(restTemplateSender, times(20)).sendToPost(anyLong(), anyString(), eq(null));
	// }
	//
	// @DisplayName("item processor가 작동하여 멱등 키를 추가한다.")
	// @Test
	// void test3() throws Exception {
	// 	// given
	// 	Date date = Date.valueOf(REQUEST_DATE.toLocalDate());
	// 	JobParameters jobParameters = new JobParametersBuilder()
	// 		.addLong("jobId", System.currentTimeMillis())
	// 		.addDate("paymentDate", date)
	// 		.toJobParameters();
	//
	// 	StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
	// 	StepSynchronizationManager.register(stepExecution);
	//
	// 	SubscriptionOrderVO order;
	// 	var list = new ArrayList<SubscriptionOrderVO>();
	// 	// when
	// 	while ((order = itemReader.read()) != null) {
	//
	// 		list.add(paymentItemProcessor.process(order));
	// 	}
	//
	// 	// then
	// 	assertThat(list).hasSize(20)
	// 		.extracting("idempotencyKey")
	// 		.isNotNull()
	// 		.doesNotHaveDuplicates();
	// }
	//
	//
	//
}