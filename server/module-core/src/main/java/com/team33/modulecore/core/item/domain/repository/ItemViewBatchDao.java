package com.team33.modulecore.core.item.domain.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemViewBatchDao {

	private final JdbcTemplate jdbcTemplate;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	public void updateAll(Map<String, Long> itemsView) {
		int batchCount = 0;
		Map<String, Long> subItemsView = new HashMap<>(104);

		for (int i = 1; i < itemsView.size() + 1; i++) {
			subItemsView.put(String.valueOf(i), itemsView.get(String.valueOf(i)));
			if (i % batchSize == 0) {
				batchCount = batchInsert(subItemsView, batchCount);
			}
		}

		if (!subItemsView.isEmpty()) {
			batchCount = batchInsert(subItemsView, batchCount);
		}
		log.info("batchCount : {}", batchCount);
	}

	private int batchInsert(Map<String, Long> subItemsView, int batchCount) {
		jdbcTemplate.batchUpdate(
			"update item set view = view + ? where item_id = ? ",
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setLong(1, subItemsView.get(String.valueOf(i + 1)));
					log.info("view count = {}", subItemsView.get(String.valueOf(i + 1)));
					ps.setLong(2, Long.parseLong(String.valueOf(i + 1)));
					log.info("itme id = {}", Long.parseLong(String.valueOf(i + 1)));
				}

				@Override
				public int getBatchSize() {
					return subItemsView.size();
				}
			}
		);
		subItemsView.clear();
		batchCount++;
		return batchCount;
	}
}
