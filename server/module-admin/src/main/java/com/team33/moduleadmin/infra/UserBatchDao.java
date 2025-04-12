package com.team33.moduleadmin.infra;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.core.user.domain.entity.User;

@Repository
public class UserBatchDao extends AbstractBatchDao<User> {

	public UserBatchDao(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	@Override
	protected String getInsertSql() {
		return "insert into users ("
			+ "email, display_name, phone, password, city, detail_address, real_name, "
			+ "roles, user_status, subscription_cart_id, normal_cart_id, oauth_id) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
    protected void setStatementValues(PreparedStatement ps, User user, int index) throws SQLException {
		int paramIndex = 1;
        ps.setString(paramIndex++, user.getEmail());
        ps.setString(paramIndex++, user.getDisplayName());
        ps.setString(paramIndex++, user.getPhone());
        ps.setString(paramIndex++, user.getPassword());
        ps.setString(paramIndex++, user.getAddress().getCity());
        ps.setString(paramIndex++, user.getAddress().getDetailAddress());
        ps.setString(paramIndex++, user.getRealName());
        ps.setString(paramIndex++, String.valueOf(user.getRoles()));
        ps.setString(paramIndex++, String.valueOf(user.getUserStatus()));
        ps.setString(paramIndex++, String.valueOf(index));
        ps.setString(paramIndex++, String.valueOf(index + 1));
        ps.setString(paramIndex++, null);
    }
}
