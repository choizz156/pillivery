package com.team33.moduleapi;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorType;
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

	@Override
	public void afterPropertiesSet() throws Exception {
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();

		tableNames = entities.stream()
			.filter(e -> isEntity(e) && hasTableAnnotation(e))
			.map(e -> {
				String tableNames = e.getJavaType().getAnnotation(Table.class).name();
				return tableNames.isBlank() ? CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()) :
					tableNames;
			})
			.collect(Collectors.toList());

		List<String> onlyEntity = entities.stream()
			.filter(e -> isEntity(e) && !hasTableAnnotation(e))
			.map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, e.getName()))
			.collect(Collectors.toList());

		tableNames.addAll(onlyEntity);
	}

	@Transactional
	public void execute(){
		em.flush();
		em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

		for (String tableName : tableNames) {
			em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();

			if(tableName.endsWith("s")){
				setColumn(tableName, tableName.substring(0, tableName.length() - 1));
			}else{
				setColumn(tableName, tableName);
			}
		}
		em.createNativeQuery("TRUNCATE TABLE item_category").executeUpdate();
		em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}

	private void setColumn(String tableName, String idName) {
		em.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN " + idName + "_id" + " RESTART WITH 1").executeUpdate();
	}

	private boolean isEntity(EntityType<?> e) {
		if(e.getJavaType().getAnnotation(DiscriminatorValue.class) != null){
			return false;
		}
		return e.getJavaType().getAnnotation(Entity.class) != null;
	}

	private boolean hasTableAnnotation(EntityType<?> e) {
		return e.getJavaType().getAnnotation(Table.class) != null;
	}
}
