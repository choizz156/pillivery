package com.team33.modulebatch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

public class CustomBatchConfigurer extends DefaultBatchConfigurer {

	private final DataSource batchDataSource;
	private final PlatformTransactionManager platformTransactionManager;

	public CustomBatchConfigurer(
		@Qualifier("batchDataSource") DataSource batchDataSource,
		@Qualifier("batchTransactionManager") PlatformTransactionManager platformTransactionManager
	) {

		this.batchDataSource = batchDataSource;
		this.platformTransactionManager = platformTransactionManager;
	}


	@Override
	protected JobExplorer createJobExplorer() throws Exception {

		JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
		jobExplorerFactoryBean.setDataSource(batchDataSource);
		jobExplorerFactoryBean.afterPropertiesSet();

		return jobExplorerFactoryBean.getObject();
	}

	@Override
	protected JobLauncher createJobLauncher() throws Exception {

		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();

		jobLauncher.setJobRepository(createJobRepository());
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		jobLauncher.afterPropertiesSet();

		return jobLauncher;
	}

	@Override
	protected JobRepository createJobRepository() throws Exception {

		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();

		factory.setDataSource(batchDataSource);
		factory.setTransactionManager(platformTransactionManager);
		factory.setDatabaseType(DatabaseType.MYSQL.getProductName());

		factory.afterPropertiesSet();
		return factory.getObject();
	}

}
