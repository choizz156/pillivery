package com.team33.modulebatch.config;

import java.util.HashMap;
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
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.team33.modulebatch.OrderVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class PaymentStepConfig {

	private static final int CHUNK_SIZE = 20;

	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;

	@Bean
	public Step paymentJobStep() throws Exception {
		return stepBuilderFactory.get("paymentJobStep")
			.<OrderVO, OrderVO>chunk(CHUNK_SIZE)
			.reader(itemReader(null))
			.processor(itemProcessor())
			.writer(itemWriter())
			.build();
	}

	private ItemWriter<? super OrderVO> itemWriter() {
		return null;
	}

	private ItemProcessor<? super OrderVO, ? extends OrderVO> itemProcessor() {
		return null;
	}

	@Profile("local")
	@Bean
	@StepScope
	public ItemReader<OrderVO> itemReader(@Value("#{jobParameters['requestDate']}") String requestDate) throws Exception {

		JdbcPagingItemReader<OrderVO> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(dataSource);
		reader.setPageSize(CHUNK_SIZE);
		reader.setRowMapper(new BeanPropertyRowMapper<>(OrderVO.class));

		H2PagingQueryProvider queryProvider = new H2PagingQueryProvider();
		queryProvider.setSelectClause("o.id");
		queryProvider.setFromClause("from order_item oi inner join orders o on o.id = oi.order_id");
		queryProvider.setWhereClause("where o.is_subscription = true and oi.payment_date = :requestDate");

		queryProvider.setSortKeys(Map.of("o.id", Order.ASCENDING));

		reader.setParameterValues(QueryGenerator.getParameterForQuery("request_date", requestDate));
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();

		return reader;
	}

}
