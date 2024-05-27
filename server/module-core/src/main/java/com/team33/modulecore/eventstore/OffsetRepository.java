package com.team33.modulecore.eventstore;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface OffsetRepository extends Repository<OffsetEntity, Long> {

	OffsetEntity findTopByOrderByCreatedAtDesc();

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("update OffsetEntity o set o.nextOffset = :nextOffset")
	void update(@Param("nextOffset") long nextOffset);
}
