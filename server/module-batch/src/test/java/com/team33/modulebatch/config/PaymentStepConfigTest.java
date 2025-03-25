package com.team33.modulebatch.config;

import static org.assertj.core.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.stream.LongStream;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulebatch.DataCleaner;
import com.team33.modulebatch.FixtureMonkeyFactory;
import com.team33.modulebatch.OrderVO;
import com.team33.modulecore.config.redis.EmbededRedisConfig;

import com.navercorp.fixturemonkey.FixtureMonkey;

/**
 * The type Payment step config test.
 */
// @EnableJpaRepositories(
// 	basePackages = {"com.team33.modulecore"}
// )
// @EntityScan(basePackages = {"com.team33.modulecore"})
@SpringBatchTest
@SpringBootTest(classes = {PaymentStepConfig.class, PaymentJobConfig.class, EmbededRedisConfig.class, DataCleaner.class})
@EnableAutoConfiguration
@EnableBatchProcessing
@ActiveProfiles("test")
	// @Sql("classpath:data.sql")
class PaymentStepConfigTest {

	private static final ZonedDateTime REQUEST_DATE = ZonedDateTime.now();
	private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkeyFactory.get();

	@Autowired
	private ItemReader<OrderVO> itemReader;

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataCleaner dataCleaner;

	/**
	 * Tear down.
	 */
	@AfterEach
	void tearDown() {
		dataCleaner.execute();
	}

	@BeforeEach
	void setUpEach() throws Exception {
		dataCleaner.afterPropertiesSet();
		jdbcTemplate = new JdbcTemplate(dataSource);
		insertTestData(100);
	}

	private void insertTestData(int count) {
		LongStream.rangeClosed(1, count).forEach(orderId -> {
			boolean isSubscription = orderId % 2 == 0;

			OrderVO order = FIXTURE_MONKEY.giveMeBuilder(OrderVO.class)
				.set("orderId", orderId)
				.set("subscription", isSubscription)
				.sample();

			jdbcTemplate.update(
				"INSERT INTO orders (id,subscription) VALUES (?, ?)",
				order.getOrderId(),
				order.isSubscription()
			);

			ZonedDateTime paymentDate = orderId % 5 == 0 ?
				FIXTURE_MONKEY.giveMeOne(ZonedDateTime.class) :
				REQUEST_DATE;

			jdbcTemplate.update(
				"INSERT INTO order_item (order_id, payment_date) VALUES (?, ?)",
				order.getOrderId(),
				java.sql.Date.valueOf(paymentDate.toLocalDate())
			);
		});
	}


	@Test
	void testSubscriptionOrderReader() throws Exception {
		java.sql.Date date = java.sql.Date.valueOf(REQUEST_DATE.toLocalDate());
		JobParameters jobParameters = new JobParametersBuilder()
			.addDate("paymentDate", date)
			.toJobParameters();

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
		StepSynchronizationManager.register(stepExecution);

		int count = 0;
		while(itemReader.read() != null) {
			count++;
		}

		assertThat(count).isEqualTo(40);

	}

}