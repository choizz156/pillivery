package com.team33.modulebatch.config;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.web.client.HttpServerErrorException;

import com.team33.modulebatch.domain.ErrorItemRepository;
import com.team33.modulebatch.exception.BatchApiException;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulebatch.listener.ItemSkipListener;
import com.team33.modulebatch.listener.PaymentStepExecutionListener;
import com.team33.modulebatch.step.PaymentItemProcessor;
import com.team33.modulebatch.step.PaymentWriter;
import com.team33.modulebatch.step.SubscriptionOrderVO;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;

@Configuration
public class PaymentStepConfig {

	private static final long BACK_OFF_PERIOD = 3000L;
	private static final int CHUNK_SIZE = 20;
	private static final int RETRY_LIMIT = 3;
	private static final int SKIP_LIMIT = 10;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

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

	@Autowired
	private SubscriptionOrderService subscriptionOrderService;

	@Bean
	public Step paymentJobStep() throws Exception {

		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(BACK_OFF_PERIOD);

		return stepBuilderFactory.get("paymentJobStep")
			.<SubscriptionOrderVO, SubscriptionOrderVO>chunk(CHUNK_SIZE)
			.reader(itemReader(null))
			.processor(itemProcessor(null))
			.writer(itemWriter())
			.faultTolerant()
			.skipLimit(SKIP_LIMIT)
			.skip(BatchApiException.class)
			.skip(DataAccessException.class)
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
		paymentItemProcessor.initialize(jobId);
		return paymentItemProcessor;
	}

	@Bean
	public ItemWriter<SubscriptionOrderVO> itemWriter() {
		paymentWriter.initialize(paymentApiDispatcher, subscriptionOrderService);	
		return paymentWriter;
	}

	@Bean
	@StepScope
	public ItemReader<SubscriptionOrderVO> itemReader(@Value("#{jobParameters['paymentDate']}") Date paymentDate)
		throws Exception {

		JdbcPagingItemReader<SubscriptionOrderVO> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(dataSource);
		reader.setPageSize(CHUNK_SIZE);
		reader.setRowMapper(new BeanPropertyRowMapper<>(SubscriptionOrderVO.class));

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

		queryProvider.setSelectClause(
			"subscription_order_id as subscriptionOrderId, subscription as subscription, next_payment_date as nextPaymentDate"
		);
		queryProvider.setFromClause("from subscription_order so");
		queryProvider.setWhereClause("where so.subscription = true and so.next_payment_date = :paymentDate");

		queryProvider.setSortKeys(Map.of("subscription_order_id", Order.ASCENDING));

		reader.setParameterValues(Collections.singletonMap("paymentDate", paymentDate));
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();

		return reader;
	}

}
