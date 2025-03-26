package com.team33.modulebatch.reader;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.team33.modulebatch.OrderVO;

public class PaymentItemReader implements ItemReader<OrderVO> {

	private static final int CHUNK_SIZE = 20;

	private final Date paymentDate;
	private final DataSource dataSource;
	private final JdbcPagingItemReader<OrderVO> reader;

	public PaymentItemReader(Date paymentDate, DataSource dataSource) {
		this.paymentDate = paymentDate;
		this.dataSource = dataSource;
		this.reader = new JdbcPagingItemReader<>();
	}

	@Override
	public OrderVO read() throws Exception {

		reader.setDataSource(dataSource);
		reader.setPageSize(CHUNK_SIZE);
		reader.setRowMapper(new BeanPropertyRowMapper<>(OrderVO.class));

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause(
			"order_id as orderId, subscription as subscription, next_payment_date as nextPaymentDay"
		);
		queryProvider.setFromClause("from order_item oi inner join orders o on o.id = oi.order_id");
		queryProvider.setWhereClause("where o.subscription = true and oi.next_payment_date = :paymentDate");

		queryProvider.setSortKeys(Map.of("order_id", Order.ASCENDING));

		reader.setParameterValues(Collections.singletonMap("paymentDate", paymentDate));
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();

		return reader.read();
	}
}
