package com.team33.modulebatch.config;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.team33.modulebatch.OrderVO;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulebatch.writer.PaymentWriter;

import lombok.RequiredArgsConstructor;

@Configuration
public class PaymentStepConfig {

	private static final int CHUNK_SIZE = 20;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DataSource dataSource;

	@Autowired
	private PaymentApiDispatcher paymentApiDispatcher;

	@Bean
	public Step paymentJobStep() throws Exception {
		return stepBuilderFactory.get("paymentJobStep")
			.<OrderVO, OrderVO>chunk(CHUNK_SIZE)
			.reader(itemReader(null))
			.writer(itemWriter(paymentApiDispatcher))
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

		JdbcPagingItemReader<OrderVO> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(dataSource);
		reader.setPageSize(CHUNK_SIZE);
		reader.setRowMapper(new BeanPropertyRowMapper<>(OrderVO.class));

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("order_id as orderId, subscription as subscription, next_payment_day as nextPaymentDay");
		queryProvider.setFromClause("from order_item oi inner join orders o on o.id = oi.order_id");
		queryProvider.setWhereClause("where o.subscription = true and oi.next_payment_day = :paymentDate");

		queryProvider.setSortKeys(Map.of("order_id", Order.ASCENDING));

		reader.setParameterValues(Collections.singletonMap("paymentDate", paymentDate));
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();

		return reader;
	}

}
