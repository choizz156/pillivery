package com.team33.modulebatch.config.step;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.web.client.HttpServerErrorException;

import com.team33.modulebatch.domain.ErrorItemRepository;
import com.team33.modulebatch.exception.ClientPaymentException;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulebatch.listener.ItemSkipListener;
import com.team33.modulebatch.listener.PaymentStepExecutionListener;
import com.team33.modulebatch.step.PaymentItemProcessor;
import com.team33.modulebatch.step.PaymentWriter;
import com.team33.modulebatch.step.SubscriptionOrderVO;

@Configuration
public class PaymentStepConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");

	private static final int CHUNK_SIZE = 20;
	private static final int RETRY_LIMIT = 3;
	private static final int SKIP_LIMIT = 10;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Qualifier("mainDataSource")
	@Autowired
	private DataSource dataSource;

	@Autowired
	private ErrorItemRepository errorItemRepository;

	@Autowired
	private PaymentWriter paymentWriter;

	@Autowired
	private PaymentItemProcessor paymentItemProcessor;

	@Autowired
	private PaymentApiDispatcher paymentApiDispatcher;

	@Bean
	public Step paymentJobStep() throws Exception {

		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(1000L);
		backOffPolicy.setMultiplier(2.0);
		backOffPolicy.setMaxInterval(5 * 60000L);

		return stepBuilderFactory.get("paymentJobStep")
			.<SubscriptionOrderVO, SubscriptionOrderVO>chunk(CHUNK_SIZE)
			.reader(itemReader(null))
			.processor(itemProcessor(null))
			.writer(itemWriter())
			.faultTolerant()
			.skipLimit(SKIP_LIMIT)
			.skip(ClientPaymentException.class)
			.retryLimit(RETRY_LIMIT)
			.retry(HttpServerErrorException.class)
			.backOffPolicy(backOffPolicy)
			.listener(new ItemSkipListener(errorItemRepository))
			.listener(new PaymentStepExecutionListener())
			.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<SubscriptionOrderVO, SubscriptionOrderVO> itemProcessor(
		@Value("#{jobParameters['jobId']}") Long jobId
	) {
		LOGGER.info("ItemProcessor");
		paymentItemProcessor.initialize(jobId);
		return paymentItemProcessor;
	}

	@Bean
	public ItemWriter<SubscriptionOrderVO> itemWriter() {
		LOGGER.info("ItemWriter");

		paymentWriter.initialize(paymentApiDispatcher);
		return paymentWriter;
	}

	@Bean
	@StepScope
	public ItemReader<SubscriptionOrderVO> itemReader(@Value("#{jobParameters['paymentDate']}") Date paymentDate)
		throws Exception {
		LOGGER.info("item reader");
		LOGGER.warn("paymentDate = {}", paymentDate);

		JdbcPagingItemReader<SubscriptionOrderVO> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(dataSource);
		reader.setPageSize(CHUNK_SIZE);
		reader.setRowMapper(new BeanPropertyRowMapper<>(SubscriptionOrderVO.class));

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

		queryProvider.setSelectClause(
			"subscription_order_id as  subscription_order_id, subscription as subscription, next_payment_date as next_payment_date"
		);
		queryProvider.setFromClause("from subscription_order so");
		queryProvider.setWhereClause(
			"where so.subscription = true and so.next_payment_date >= :startDate and so.next_payment_date < :endDate"
		);

		LocalDate localDate = paymentDate.toLocalDate();
		LocalDateTime startOfDay = localDate.atStartOfDay();
		LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay();

		queryProvider.setSortKeys(Map.of("subscription_order_id", Order.ASCENDING));

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startDate", Timestamp.valueOf(startOfDay));
		paramMap.put("endDate", Timestamp.valueOf(endOfDay));

		reader.setParameterValues(paramMap);
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();

		return reader;
	}

}
