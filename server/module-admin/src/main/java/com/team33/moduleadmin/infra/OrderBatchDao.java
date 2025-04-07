package com.team33.moduleadmin.infra;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.core.order.domain.entity.Order;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OrderBatchDao extends AbstractBatchDao<Order> {

    public OrderBatchDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO orders ("
                + "created_at, updated_at, is_ordered_at_cart, main_item_name, "
                + "order_status, sid, tid, expect_price, total_discount_price, "
                + "total_price, city, detail_address, phone, real_name, "
                + "total_quantity, user_id, total_items_count) "
                + "VALUES (now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setStatementValues(PreparedStatement ps, Order order, int index) throws SQLException {
        int paramIndex = 1;
        ps.setBoolean(paramIndex++, order.isOrderedAtCart());
        ps.setString(paramIndex++, order.getMainItemName());
        ps.setString(paramIndex++, order.getOrderStatus().name());
        ps.setString(paramIndex++, order.getSid());
        ps.setString(paramIndex++, order.getTid());
        ps.setInt(paramIndex++, order.getExpectPrice());
        ps.setInt(paramIndex++, order.getPrice().getTotalDiscountPrice());
        ps.setInt(paramIndex++, order.getTotalPrice());
        ps.setString(paramIndex++, order.getReceiver().getAddress().getCity());
        ps.setString(paramIndex++, order.getReceiver().getAddress().getDetailAddress());
        ps.setString(paramIndex++, order.getReceiver().getPhone());
        ps.setString(paramIndex++, order.getReceiver().getRealName());
        ps.setInt(paramIndex++, order.getTotalQuantity());
        ps.setLong(paramIndex++, order.getUserId());
        ps.setInt(paramIndex, order.getTotalItemsCount());
    }
}