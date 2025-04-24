package com.team33.modulecore.core.item.domain.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class ItemSalesBatchDao {

	private final JdbcTemplate jdbcTemplate;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	public void updateAll(Map<Long, Long> itemsView) {

		int batchCount = 0;
		Map<Long, Long> subItemsView = new HashMap<>(104);

		for (Map.Entry<Long, Long> entry : itemsView.entrySet()) {
			subItemsView.put(entry.getKey(), entry.getValue());
			if (subItemsView.size() % batchSize == 0) {
				batchCount = batchInsert(subItemsView, batchCount);
			}
		}

		if (!subItemsView.isEmpty()) {
			batchCount = batchInsert(subItemsView, batchCount);
		}
		log.info("batchCount : {}", batchCount);
	}

	private int batchInsert(Map<Long, Long> subItemsView, int batchCount) {
		List<Map.Entry<Long, Long>> entries = new ArrayList<>(subItemsView.entrySet());
		jdbcTemplate.batchUpdate(
			"update item set sales = ? where item_id = ? ",
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Map.Entry<Long, Long> entry = entries.get(i);
					ps.setLong(1, entry.getValue());
					ps.setLong(2, entry.getKey());
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
