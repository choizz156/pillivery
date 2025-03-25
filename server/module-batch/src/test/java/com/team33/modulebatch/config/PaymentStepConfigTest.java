package com.team33.modulebatch.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulebatch.DataCleaner;
import com.team33.modulebatch.FixtureMonkeyFactory;
import com.team33.modulebatch.OrderVO;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulecore.config.redis.EmbededRedisConfig;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SpringBatchTest
@SpringBootTest(classes = {
	BatchTestConfig.class,
	PaymentJobConfig.class,
	EmbededRedisConfig.class,
	DataCleaner.class,
	PaymentApiDispatcher.class
})
@EnableAutoConfiguration
@EnableBatchProcessing
@ActiveProfiles("test")
class PaymentStepConfigTest {

	private static final ZonedDateTime REQUEST_DATE = ZonedDateTime.now();
	private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkeyFactory.get();

	@Autowired
	private ItemReader<OrderVO> itemReader;

	@Autowired
	private ItemWriter<OrderVO> itemWriter;

	@Autowired
	private PaymentApiDispatcher mockPaymentApiDispatcher;

	@MockBean
	private RestTemplateSender restTemplateSender;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataCleaner dataCleaner;

	@BeforeEach
	void setUpEach() throws Exception {
		dataCleaner.afterPropertiesSet();
		jdbcTemplate = new JdbcTemplate(dataSource);
		insertTestData(100);
	}

	@AfterEach
	void tearDown() {
		dataCleaner.execute();
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
	void testItemWriterWritesCorrectly() throws Exception {
		// given
		java.sql.Date date = java.sql.Date.valueOf(REQUEST_DATE.toLocalDate());
		JobParameters jobParameters = new JobParametersBuilder()
			.addDate("paymentDate", date)
			.toJobParameters();

		StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
		StepSynchronizationManager.register(stepExecution);

		OrderVO order;
		var list = new ArrayList<OrderVO>();
		while ((order = itemReader.read()) != null) {
			list.add(order);
		}

		//when
		itemWriter.write(list);

		// then
		verify(restTemplateSender, times(40)).sendToPost(anyString(),eq(null),eq(null), eq(String.class));
	}
}