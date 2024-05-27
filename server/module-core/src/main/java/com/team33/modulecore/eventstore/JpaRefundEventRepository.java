package com.team33.modulecore.eventstore;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class JpaRefundEventRepository implements EventsRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void save(Object event) {
		em.persist(event);
	}

	@Override
	public List<ApiEventSet> get(long offset, long limit) {
		return em.createQuery("select a from ApiEventSet a", ApiEventSet.class)
			.setMaxResults((int)limit)
			.setFirstResult((int)offset)
			.getResultList();
	}
}
