package com.team33.modulebatch;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.CaseFormat;

@Component
public class DataCleaner implements InitializingBean {

	@PersistenceContext
	private EntityManager em;

	private List<String> tableNames;
	private List<String> collectionTableNames;

	@Override
	public void afterPropertiesSet() throws Exception {
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();

		tableNames = entities.stream()
				.filter(e -> isEntity(e) && hasTableAnnotation(e))
				.map(e -> {
					String name = e.getJavaType().getAnnotation(Table.class).name();
					return name.isBlank()
							? CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName())
							: name;
				})
				.collect(Collectors.toList());

		List<String> onlyEntity = entities.stream()
				.filter(e -> isEntity(e) && !hasTableAnnotation(e))
				.map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, e.getName()))
				.collect(Collectors.toList());

		tableNames.addAll(onlyEntity);

		tableNames.addAll(List.of(
				"BATCH_JOB_INSTANCE",
				"BATCH_JOB_EXECUTION",
				"BATCH_JOB_EXECUTION_PARAMS",
				"BATCH_STEP_EXECUTION",
				"BATCH_STEP_EXECUTION_CONTEXT",
				"BATCH_JOB_EXECUTION_CONTEXT"));

		collectionTableNames = entities.stream()
				.flatMap(e -> Arrays.stream(e.getJavaType().getDeclaredFields()))
				.filter(field -> field.getAnnotation(CollectionTable.class) != null)
				.map(field -> field.getAnnotation(CollectionTable.class).name())
				.collect(Collectors.toList());
	}

	@Transactional
	public void execute() {
		em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

		tableNames.forEach(
				table -> em.createNativeQuery("TRUNCATE TABLE " + table + " RESTART IDENTITY").executeUpdate());

		collectionTableNames.forEach(
				table -> em.createNativeQuery("TRUNCATE TABLE " + table + " RESTART IDENTITY").executeUpdate());

		em.createNativeQuery("TRUNCATE TABLE subscription_order RESTART IDENTITY").executeUpdate();
		em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}


	private boolean isEntity(EntityType<?> e) {

		if (e.getJavaType().getAnnotation(DiscriminatorValue.class) != null) {
			return false;
		}
		return e.getJavaType().getAnnotation(Entity.class) != null;
	}

	private boolean hasTableAnnotation(EntityType<?> e) {
		return e.getJavaType().getAnnotation(Table.class) != null;
	}
}
