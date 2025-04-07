package com.team33.moduleadmin.infra;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractBatchDao<T> {

	protected final JdbcTemplate jdbcTemplate;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	public void saveAll(List<T> entities) {

		log.info("batch size : {}", batchSize);
		log.info("entities size : {}", entities.size());
		int batchCount = 0;
		List<T> subEntities = new ArrayList<>(batchSize + 4);

		for (int i = 0; i < entities.size(); i++) {
			subEntities.add(entities.get(i));
			if ((i + 1) % batchSize == 0) {
				batchCount = batchInsert(subEntities, batchCount);
			}
		}

		if (!subEntities.isEmpty()) {
			batchCount = batchInsert(subEntities, batchCount);
		}
		log.info("batchCount : {}", batchCount);
	}

	private int batchInsert(List<T> subEntities, int batchCount) {
		try {
			jdbcTemplate.batchUpdate(
			   getInsertSql(),
			   new BatchPreparedStatementSetter() {
				   @Override
				   public void setValues(PreparedStatement ps, int i) throws SQLException {

					   try {
						   setStatementValues(ps, subEntities.get(i), i);
					   } catch (Exception e) {
						   log.error("Error in batch insert", e);
					   }
				   }

				   @Override
				   public int getBatchSize() {
					   return subEntities.size();
				   }
			   }
		   );
		} catch (DataAccessException e) {
			log.error("여기인가?", e);
		}catch (Exception e) {
			log.error("error in batch insert", e);
		}
		subEntities.clear();
		log.info("batchCount = {}", batchCount);
		return ++batchCount;
	}

	protected abstract String getInsertSql();

	protected abstract void setStatementValues(PreparedStatement ps, T entity, int index) throws SQLException;
}