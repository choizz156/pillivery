package com.team33.moduleadmin.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.team33.modulecore.core.user.domain.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserBatchDao{

	private final JdbcTemplate jdbcTemplate;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	public void saveAll(List<User> users) {
		log.info("batch size : {}", batchSize);
		log.info("users size : {}", users.size());
		int batchCount = 0;
		List<User> subUsers = new ArrayList<>(104);
		for (int i = 0; i < users.size(); i++) {
			subUsers.add(users.get(i));
			if ((i + 1) % batchSize == 0) {
				batchCount = batchInsert(subUsers, batchCount);
			}
		}
		if(!subUsers.isEmpty()) {
			batchCount = batchInsert(subUsers, batchCount);
		}
		log.info("batchCount : {}", batchCount);
	}

	private int batchInsert(List<User> subUsers, int batchCount) {
		jdbcTemplate.batchUpdate(
			"insert into users ("
				+ "email, displayName, phone, password, city, detailAddress, realName, roles, userStatus, subscriptionCartId, normalCartId, oauthId) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, subUsers.get(i).getEmail());
					ps.setString(2, subUsers.get(i).getDisplayName());
					ps.setString(3, subUsers.get(i).getPhone());
					ps.setString(4, subUsers.get(i).getPassword());
					ps.setString(5, subUsers.get(i).getAddress().getCity());
					ps.setString(6, subUsers.get(i).getAddress().getDetailAddress());
					ps.setString(7, subUsers.get(i).getRealName());
					ps.setString(8, String.valueOf(subUsers.get(i).getRoles()));
					ps.setString(9, String.valueOf(subUsers.get(i).getUserStatus()));
					ps.setString(10, String.valueOf(i));
					ps.setString(11, String.valueOf(i + 1));
					ps.setString(12, null);
				}

				@Override
				public int getBatchSize() {
					return subUsers.size();
				}
			}
		);
		subUsers.clear();
		batchCount++;
		return batchCount;
	}
}


