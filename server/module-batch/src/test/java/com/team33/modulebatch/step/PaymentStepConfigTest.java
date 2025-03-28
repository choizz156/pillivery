package com.team33.modulebatch.step;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.LongStream;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.team33.modulebatch.BatchApiTest;
import com.team33.modulebatch.FixtureMonkeyFactory;

import com.navercorp.fixturemonkey.FixtureMonkey;

class PaymentStepConfigTest extends BatchApiTest {

	private static final ZonedDateTime REQUEST_DATE = ZonedDateTime.now();
	private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkeyFactory.get();

	@Autowired
	private ItemReader<SubscriptionOrderVO> itemReader;

	@Autowired
	private ItemWriter<SubscriptionOrderVO> itemWriter;

	@Autowired
	private ItemProcessor<SubscriptionOrderVO, SubscriptionOrderVO> itemProcessor;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUpEach() throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		insertTestData(100);
	}

	@AfterEach
	void tearDown() {
		jdbcTemplate.execute("TRUNCATE TABLE orders");
		jdbcTemplate.execute("TRUNCATE TABLE order_item");
	}

	private void insertTestData(int count) {
		LongStream.rangeClosed(1, count).forEach(orderId -> {
			boolean isSubscription = orderId % 2 == 0;

			SubscriptionOrderVO order = FIXTURE_MONKEY.giveMeBuilder(SubscriptionOrderVO.class)
				.set("orderId", orderId)
				.set("subscription", isSubscription)
				.sample();

			jdbcTemplate.update(
				"INSERT INTO orders (id, subscription) VALUES (?, ?)",
				order.getSubscriptionOrderId(),
				order.isSubscription()
			);

			ZonedDateTime paymentDate = orderId % 5 == 0 ?
				FIXTURE_MONKEY.giveMeOne(ZonedDateTime.class) :
				REQUEST_DATE;

			jdbcTemplate.update(
				"INSERT INTO order_item (order_id, next_payment_date) VALUES (?, ?)",
				order.getSubscriptionOrderId(),
				java.sql.Date.valueOf(paymentDate.toLocalDate())
			);
		});
	}

	@DisplayName("item reader가 작동한다. (총 개수 100개, 읽어올 아이템 40개)")
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

		assertThat(count).isEqualTo(40);

	}

	@DisplayName("item writer가 작동하여 서버에 요청을 보낸다.(40개)")
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

		//when
		itemWriter.write(list);

		// then
		verify(restTemplateSender, times(40)).sendToPost(anyString(), eq(null), eq(null), eq(String.class));
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
		//when
		while ((order = itemReader.read()) != null) {
			System.out.println(order.getNextPaymentDate());
			list.add(itemProcessor.process(order));
		}

		// then
		assertThat(list).hasSize(40)
			.extracting("idempotencyKey")
			.isNotNull()
			.doesNotHaveDuplicates();
	}
}