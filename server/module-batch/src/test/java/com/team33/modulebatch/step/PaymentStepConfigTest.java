package com.team33.modulebatch.step;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.jdbc.core.JdbcTemplate;

import com.team33.modulebatch.BatchApiTest;


class PaymentStepConfigTest extends BatchApiTest {

	private static final ZonedDateTime REQUEST_DATE = ZonedDateTime.now();

	@Autowired
	private ItemReader<SubscriptionOrderVO> itemReader;

	@Autowired
	private ItemWriter<SubscriptionOrderVO> itemWriter;


	@Autowired
	@Qualifier("mainDataSource")
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUpEach() throws Exception {

		jdbcTemplate = new JdbcTemplate(dataSource);
		insertTestData();
	}

	@AfterEach
	void tearDown() {

		jdbcTemplate.execute("TRUNCATE TABLE subscription_order");
	}

	@DisplayName("item reader가 작동한다. (총 개수 100개, 읽어올 아이템 20개)")
	@Test
	void test1() throws Exception {

		java.sql.Date date = java.sql.Date.valueOf(REQUEST_DATE.toLocalDate());
		JobParameters jobParameters = new JobParametersBuilder()
			.addDate("paymentDate", date)
			.toJobParameters();

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
		StepSynchronizationManager.register(stepExecution);

		int count = 0;
		while (itemReader.read() != null) {
			count++;
		}

		assertThat(count).isEqualTo(20);
	}

	@DisplayName("item writer가 작동하여 서버에 요청을 보낸다.(20개)")
	@Test
	void test2() throws Exception {
		// given
		java.sql.Date date = java.sql.Date.valueOf(REQUEST_DATE.toLocalDate());
		JobParameters jobParameters = new JobParametersBuilder()
			.addDate("paymentDate", date)
			.toJobParameters();

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
		StepSynchronizationManager.register(stepExecution);

		SubscriptionOrderVO order;
		var list = new ArrayList<SubscriptionOrderVO>();
		while ((order = itemReader.read()) != null) {
			list.add(order);
		}

		list.forEach(subscriptionOrderVO -> {
			paymentItemProcessor.initialize(1L);
			paymentItemProcessor.process(subscriptionOrderVO);
		});

		// when
		itemWriter.write(list);

		// then
		verify(restTemplateSender, times(20)).sendToPost(anyString(), anyString(), eq(null));
	}

	@DisplayName("item processor가 작동하여 멱등 키를 추가한다.")
	@Test
	void test3() throws Exception {
		// given
		java.sql.Date date = java.sql.Date.valueOf(REQUEST_DATE.toLocalDate());
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("jobId", System.currentTimeMillis())
			.addDate("paymentDate", date)
			.toJobParameters();

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
		StepSynchronizationManager.register(stepExecution);

		SubscriptionOrderVO order;
		var list = new ArrayList<SubscriptionOrderVO>();
		// when
		while ((order = itemReader.read()) != null) {

			list.add(paymentItemProcessor.process(order));
		}

		// then
		assertThat(list).hasSize(20)
			.extracting("idempotencyKey")
			.isNotNull()
			.doesNotHaveDuplicates();
	}

	private void insertTestData() {

		for (long i = 1; i <= 100; i++) {
			Date nextPaymentDate;
			if (i % 5 == 0) {
				nextPaymentDate = Date.valueOf(REQUEST_DATE.toLocalDate());
			} else {
				nextPaymentDate = Date.valueOf(REQUEST_DATE.toLocalDate().plusDays(1));
			}

			jdbcTemplate.update(
				"INSERT INTO subscription_order (" +
					"subscription_order_id, created_at, updated_at, " +
					"main_item_name, total_quantity, order_status, user_id, " +
					"total_price, total_discount_price, expect_price, " +
					"sid, tid, " +
					"period, subscription, next_payment_date, last_payment_date) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				i,
				java.sql.Timestamp.from(ZonedDateTime.now().toInstant()),
				java.sql.Timestamp.from(ZonedDateTime.now().toInstant()),
				"테스트 상품 " + i,
				1,
				"SUBSCRIBE",
				(i % 10 + 1),
				10000,
				1000,
				9000,
				"not_register_subscription_yet",
				"tid_" + i,
				30,
				true,
				nextPaymentDate,
				null);
		}
	}
}