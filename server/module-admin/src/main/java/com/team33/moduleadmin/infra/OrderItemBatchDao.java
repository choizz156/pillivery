package com.team33.moduleadmin.infra;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.core.order.domain.entity.OrderItem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OrderItemBatchDao extends AbstractBatchDao<OrderItem> {

	public OrderItemBatchDao(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	@Override
	protected String getInsertSql() {

		return "INSERT INTO order_item ("
			+ "subscription, period, next_payment_date,last_payment_date, quantity, item_id, order_id, created_at, updated_at) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, now(), now())";
	}

	@Override
	protected void setStatementValues(PreparedStatement ps, OrderItem entity, int index) throws SQLException {

		int paramIndex = 1;
		try {
			ps.setBoolean(paramIndex++, entity.getSubscriptionInfo().isSubscription());
			ps.setInt(paramIndex++, entity.getSubscriptionInfo().getPeriod());
			ps.setTimestamp(paramIndex++, null);
			// ps.setTimestamp(paramIndex++, Timestamp.from(entity.getSubscriptionInfo().getNextPaymentDate().toInstant()));
			// ps.setTimestamp(paramIndex++, Timestamp.from(entity.getSubscriptionInfo().getLastPaymentDate().toInstant()));
			ps.setTimestamp(paramIndex++, null);
			ps.setInt(paramIndex++, entity.getQuantity());
			ps.setLong(paramIndex++, index);
			ps.setLong(paramIndex, index);
		} catch (SQLException e) {
			log.error("sql", e);
		}

		log.error(entity.toString());
	}

}