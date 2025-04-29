package com.team33.modulebatch;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@TestConfiguration
public class TestDataSourceConfig {

	@Bean
	@Qualifier("mainDataSource")
	public DataSource mainDataSource() {

		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.build();
	}
}
