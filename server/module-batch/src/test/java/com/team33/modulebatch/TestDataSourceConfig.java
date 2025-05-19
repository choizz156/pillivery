package com.team33.modulebatch;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@TestConfiguration
public class TestDataSourceConfig {

	@Bean
	@Qualifier("mainDataSource")
	public DataSource mainDataSource() {

		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.build();
	}

	@Bean
	@Qualifier("mainEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
		@Qualifier("mainDataSource") DataSource dataSource,
		EntityManagerFactoryBuilder builder
	) {
		return builder
			.dataSource(dataSource)
			.packages("com.team33.modulecore", "com.team33.modulebatch")
			.persistenceUnit("test-persistence-unit")
			.build();
	}
}
