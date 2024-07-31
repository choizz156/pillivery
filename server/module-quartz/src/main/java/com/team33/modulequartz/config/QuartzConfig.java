package com.team33.modulequartz.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class QuartzConfig {

	@Bean
	public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(){
		return bean -> {
			bean.setDataSource(schedulerDataSource());
			bean.setTransactionManager( schedulerTransactionManager());
			bean.setQuartzProperties(quartzProperties());
		};
	}

	@Bean
	@QuartzDataSource
	@ConfigurationProperties(prefix = "spring.datasource.quartz")
	public DataSource schedulerDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public PlatformTransactionManager schedulerTransactionManager(){
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
		dataSourceTransactionManager.setDataSource(schedulerDataSource());
		return dataSourceTransactionManager;
	}

	private Properties quartzProperties() {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("application-quartz.yml"));
		Properties properties = null;
		try {
			propertiesFactoryBean.afterPropertiesSet();
			properties = propertiesFactoryBean.getObject();
		} catch (Exception e) {
			log.error("error", e);
		}
		return properties;
	}
}
