package com.team33.modulebatch.step;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentItemReader implements ItemReader<SubscriptionOrderVO> {

	private static final int CHUNK_SIZE = 20;

	private Date paymentDate;
	private DataSource dataSource;
	private JdbcPagingItemReader<SubscriptionOrderVO> reader;

	public PaymentItemReader(Date paymentDate, DataSource dataSource) {
		this.paymentDate = paymentDate;
		this.dataSource = dataSource;
		this.reader = new JdbcPagingItemReader<>();
	}

	@Override
	public SubscriptionOrderVO read() throws Exception {

		reader.setDataSource(dataSource);
		reader.setPageSize(CHUNK_SIZE);
		reader.setRowMapper(new BeanPropertyRowMapper<>(SubscriptionOrderVO.class));

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

		queryProvider.setSelectClause(
			"subscription_order_id as subscriptionOrderId, subscription as subscription, next_payment_date as nextPaymentDate"
		);
		queryProvider.setFromClause("from subscription_order so");
		queryProvider.setWhereClause("where so.subscription = true and so.next_payment_date = :paymentDate");

		queryProvider.setSortKeys(Map.of("order_id", Order.ASCENDING));

		reader.setParameterValues(Collections.singletonMap("paymentDate", paymentDate));
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();

		return reader.read();
	}
}
