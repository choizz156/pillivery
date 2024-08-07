package com.team33.modulecore.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Profile("!test")
@Configuration
public class JdbcConfig {

	@Autowired
	@Qualifier("mainDataSource")
	private DataSource mainDataSource;

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(mainDataSource);
	}

	@Bean
	public DataSource jdbcDataSource() {
		return mainDataSource;
	}
}