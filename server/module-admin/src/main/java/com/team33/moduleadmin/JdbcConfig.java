package com.team33.moduleadmin;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcConfig {
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(jdbcDataSource());
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.main")
	public DataSource jdbcDataSource() {
		return DataSourceBuilder.create().build();
	}
}
