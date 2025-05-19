package com.team33.modulebatch.config.job;

import static org.assertj.core.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.team33.modulebatch.BatchTest;
import com.team33.modulebatch.domain.DelayedItemRepository;
import com.team33.modulebatch.domain.entity.DelayedItem;
import com.team33.modulebatch.infra.RestTemplateSender;

@ExtendWith(MockitoExtension.class)
class PaymentJobConfigTest extends BatchTest {

	@Autowired
	@Qualifier("mainDataSource")
	private DataSource dataSource;

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private DelayedItemRepository delayedItemRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;


	@MockBean
	private RestTemplateSender restTemplateSender;

	@BeforeEach
	void setUpEach(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		inputData();
	}

	@Test
	void test1() throws Exception {
		//given
		LocalDate now = LocalDate.now();
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("jobId", 1L)
			.addDate("paymentDate", Date.valueOf(now))
			.toJobParameters();
		//when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

		//then
		ExitStatus exitStatus = jobExecution.getExitStatus();
		assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
	}


	private void inputData(){
		for (long i = 1; i <= 20; i++) {
			LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(5L);
			DelayedItem delayedItem = DelayedItem.builder()
				.subscriptionOrderId(i)
				.retryCount(2L)
				.reason("test")
				.originalPaymentDate(now)
				.build();
			delayedItemRepository.save(delayedItem);
		}

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

		for (long i = 1; i <= 20; i++) {
			LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
			DelayedItem delayedItem = DelayedItem.of(i, "test", now);
			delayedItemRepository.save(delayedItem);
		}
		ZonedDateTime REQUEST_DATE = ZonedDateTime.now();

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