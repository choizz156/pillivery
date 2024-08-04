package com.team33.moduleadmin;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


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