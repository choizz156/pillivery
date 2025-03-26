package com.team33.modulebatch.config;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.backoff.FixedBackOffPolicy;

import com.team33.modulebatch.OrderVO;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulebatch.listener.ItemSkipListener;
import com.team33.modulebatch.reader.PaymentItemReader;
import com.team33.modulebatch.writer.PaymentWriter;
import com.team33.moduleexternalapi.exception.PaymentApiException;

@Configuration
public class PaymentStepConfig {

	private static final int CHUNK_SIZE = 20;
	private static final int SKIP_LIMIT = 10;
	private static final int RETRY_LIMIT = 3;
	private static final long BACK_OFF_PERIOD = 3000L;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private PaymentApiDispatcher paymentApiDispatcher;

	@Autowired
	private DataSource dataSource;

	@Bean
	public Step paymentJobStep() throws Exception {

		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(BACK_OFF_PERIOD);

		return stepBuilderFactory.get("paymentJobStep")
			.<OrderVO, OrderVO>chunk(CHUNK_SIZE)
			.reader(itemReader(null))
			.writer(itemWriter(paymentApiDispatcher))
			.listener(new ItemSkipListener())
			.faultTolerant()
			.skipLimit(SKIP_LIMIT)
			.skip(PaymentApiException.class)
			.skip(DataAccessException.class)
			.retryLimit(RETRY_LIMIT)
			.retry(PaymentApiException.class)
			.backOffPolicy(backOffPolicy)
			.build();
	}

	@Bean
	public ItemWriter<OrderVO> itemWriter(PaymentApiDispatcher paymentApiDispatcher) {
		return new PaymentWriter(paymentApiDispatcher);
	}

	@Bean
	@StepScope
	public ItemReader<OrderVO> itemReader(@Value("#{jobParameters['paymentDate']}") Date paymentDate) throws
		Exception {
		return new PaymentItemReader(paymentDate, dataSource);
	}

}
