package com.team33.modulebatch.config.step;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.team33.modulebatch.BatchTest;
import com.team33.modulebatch.domain.DelayedItemRepository;
import com.team33.modulebatch.domain.ErrorStatus;
import com.team33.modulebatch.domain.entity.DelayedItem;
import com.team33.modulebatch.step.RetryItemProcessor;

@DisplayName("2번째 결제 재시도")
class RetryStepConfig2Test extends BatchTest {

	@Autowired
	private DelayedItemRepository delayedItemRepository;

	@Autowired
	private RetryItemProcessor retryItemProcessor;

	@Autowired
	@Qualifier("delayedItemReader2")
	private ItemReader<DelayedItem> delayedItemReader2;

	@Autowired
	@Qualifier("delayedItemWriter2")
	private ItemWriter<DelayedItem> delayedItemWriter;

	@BeforeEach
	void setUpEach() throws Exception {

		insertTestData();
	}

	@DisplayName("item reader가 작동한다. (읽어올 아이템 20개)")
	@Test
	void test1() throws Exception {

		JpaPagingItemReader<DelayedItem> reader = (JpaPagingItemReader<DelayedItem>)delayedItemReader2;

		reader.afterPropertiesSet();
		reader.open(new ExecutionContext());

		int count = 0;
		while (reader.read() != null) {
			count++;
		}

		reader.close();

		assertThat(count).isEqualTo(20);
	}

	@DisplayName("읽어 온 아이템을 외부 api와 통신할 수 있다.")
	@Test
	void test2() throws Exception {

		retryItemProcessor.initialize(1L);

		JpaPagingItemReader<DelayedItem> reader = (JpaPagingItemReader<DelayedItem>)delayedItemReader2;

		reader.afterPropertiesSet();
		reader.open(new ExecutionContext());

		DelayedItem delayedItem;
		var list = new ArrayList<DelayedItem>(20);
		while ((delayedItem = reader.read()) != null) {
			list.add(retryItemProcessor.process(delayedItem));
		}

		reader.close();

		assertThat(list).hasSize(20)
			.extracting("idempotencyKey")
			.isNotNull()
			.doesNotHaveDuplicates();

	}

	@DisplayName("api와 통신 처리 결과를 db에 저장할 수 있다.")
	@Test
	void test3() throws Exception {
		//given
		LocalDate now = LocalDate.now();

		DelayedItem delayedItem = DelayedItem.builder()
			.subscriptionOrderId(1L)
			.status(ErrorStatus.DELAYED)
			.originalPaymentDate(now)
			.retryCount(1L)
			.build();

		DelayedItem solvedItem = DelayedItem.builder()
			.subscriptionOrderId(2L)
			.retryCount(1L)
			.originalPaymentDate(now)
			.status(ErrorStatus.SOLVED)
			.build();

		List<DelayedItem> list = List.of(delayedItem, solvedItem);

		//when
		delayedItemWriter.write(list);

		//then
		List<DelayedItem> result = delayedItemRepository.findByOriginalPaymentDateAndRetryCount(now, 2L);
		assertThat(result).hasSize(1);
		boolean exist = delayedItemRepository.existsDelayedItemBySubscriptionOrderIdAndStatus(2L, ErrorStatus.SOLVED);
		assertThat(exist).isTrue();
	}

	private void insertTestData() {

		for (long i = 1; i <= 20; i++) {
			LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(3L);
			DelayedItem delayedItem = DelayedItem.builder()
				.subscriptionOrderId(i)
				.retryCount(1L)
				.reason("test")
				.originalPaymentDate(now)
				.build();
			delayedItemRepository.save(delayedItem);
		}
	}
	// private void testStep() {
	// 	this.step = stepBuilderFactory.get("testStep")
	// 		.<DelayedItem, DelayedItem>chunk(20)
	// 		.reader(delayedItemReader)
	// 		.writer(delayedItemWriter)
	// 		.faultTolerant()
	// 		.build();
	// }
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