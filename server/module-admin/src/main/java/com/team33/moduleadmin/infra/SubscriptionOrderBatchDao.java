package com.team33.moduleadmin.infra;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SubscriptionOrderBatchDao extends AbstractBatchDao<SubscriptionOrder> {

	public SubscriptionOrderBatchDao(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	@Override
	protected String getInsertSql() {

		return "INSERT INTO subscription_order ("
			+ " created_at, updated_at, "
			+ "subscription, period,  "
			+ "main_item_name, order_status, sid, tid, "
			+ "expect_price, total_discount_price, total_price, "
			+ "real_name, phone, city, detail_address, "
			+ "total_quantity, user_id, order_item_id) "
			+ "VALUES (now(), now(), ?, ?, ?, ?, ?, ?, "
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected void setStatementValues(PreparedStatement ps, SubscriptionOrder entity, int index) throws SQLException {
		int paramIndex = 1;
		log.error("index = {}", index);
		// BaseEntity fields
		// ps.setTimestamp(paramIndex++, Timestamp.from(entity.getCreatedAt().toInstant()));
		// ps.setTimestamp(paramIndex++, Timestamp.from(entity.getUpdatedAt().toInstant()));
		// SubscriptionInfo fields
		ps.setBoolean(paramIndex++, entity.getSubscriptionInfo().isSubscription());
		ps.setInt(paramIndex++, entity.getSubscriptionInfo().getPeriod());
		// ps.setTimestamp(paramIndex++, Timestamp.from(entity.getSubscriptionInfo().getNextPaymentDate().toInstant()));
		// ps.setTimestamp(paramIndex++, Timestamp.from(entity.getSubscriptionInfo().getLastPaymentDate().toInstant()));
		// OrderCommonInfo fields
		ps.setString(paramIndex++, entity.getOrderCommonInfo().getMainItemName());
		ps.setString(paramIndex++, entity.getOrderCommonInfo().getOrderStatus().name());
		ps.setString(paramIndex++, entity.getOrderCommonInfo().getPaymentToken().getSid());
		ps.setString(paramIndex++, entity.getOrderCommonInfo().getPaymentToken().getTid());
		ps.setInt(paramIndex++, entity.getOrderCommonInfo().getPrice().getExpectPrice());
		ps.setInt(paramIndex++, entity.getOrderCommonInfo().getPrice().getTotalDiscountPrice());
		ps.setInt(paramIndex++, entity.getOrderCommonInfo().getPrice().getTotalPrice());
		ps.setString(paramIndex++, entity.getOrderCommonInfo().getReceiver().getRealName());
		ps.setString(paramIndex++, entity.getOrderCommonInfo().getReceiver().getPhone());
		ps.setString(paramIndex++, entity.getOrderCommonInfo().getReceiver().getAddress().getCity());
		ps.setString(paramIndex++, entity.getOrderCommonInfo().getReceiver().getAddress().getDetailAddress());
		ps.setInt(paramIndex++, entity.getOrderCommonInfo().getTotalQuantity());
		ps.setLong(paramIndex++, entity.getOrderCommonInfo().getUserId());
		// Foreign key
		ps.setLong(paramIndex, entity.getOrderItem().getId());
	}
}