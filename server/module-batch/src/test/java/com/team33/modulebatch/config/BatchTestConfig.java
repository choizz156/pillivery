package com.team33.modulebatch.config;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.team33.modulebatch.OrderVO;

@EnableAutoConfiguration
@EnableBatchProcessing
@TestConfiguration
public class BatchTestConfig {

	private static final int CHUNK_SIZE = 20;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Bean
	public Step paymentJobStep() throws Exception {
		return stepBuilderFactory.get("paymentJobStep")
			.<OrderVO, OrderVO>chunk(CHUNK_SIZE)
			.reader(itemReader(null))
			// .processor(itemProcessor())
			.writer(itemWriter())
			.build();
	}

	@Bean
	public ItemWriter<OrderVO> itemWriter() {
		return items -> items.forEach(System.out::println);

	}

	@Profile("local || test")
	@Bean
	@StepScope
	public ItemReader<OrderVO> itemReader(@Value("#{jobParameters['paymentDate']}") Date paymentDate) throws
		Exception {

		JdbcPagingItemReader<OrderVO> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(dataSource);
		reader.setPageSize(CHUNK_SIZE);
		reader.setRowMapper(new BeanPropertyRowMapper<>(OrderVO.class));

		H2PagingQueryProvider queryProvider = new H2PagingQueryProvider();
		queryProvider.setSelectClause("order_id as orderId, subscription as subscription, payment_date as paymentDate");
		queryProvider.setFromClause("from order_item oi inner join orders o on o.id = oi.order_id");
		queryProvider.setWhereClause("where o.subscription = true and oi.payment_date = :paymentDate");

		queryProvider.setSortKeys(Map.of("order_id", Order.ASCENDING));


		reader.setParameterValues(Collections.singletonMap("paymentDate", paymentDate));
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();

		return reader;
	}
}
