package com.team33.modulebatch.config;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Profile("!test")
@Configuration
public class BatchSourceConfig {

	@Bean(name = "batchDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.batch")
	public DataSource batchDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "batchTransactionManager")
	public PlatformTransactionManager batchTransactionManager() {
		return new DataSourceTransactionManager(batchDataSource());
	}

	@Bean
	public DefaultBatchConfigurer defaultBatchConfigurer(
		@Qualifier("batchDataSource") DataSource batchDataSource,
		@Qualifier("batchTransactionManager") PlatformTransactionManager platformTransactionManager
	) {
		return new CustomBatchConfigurer(batchDataSource, platformTransactionManager);
	}
}
