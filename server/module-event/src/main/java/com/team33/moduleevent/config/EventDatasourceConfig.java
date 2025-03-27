package com.team33.moduleevent.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("!test")
@EnableJpaRepositories(
	entityManagerFactoryRef = "eventEntityManagerFactory",
	transactionManagerRef = "eventTransactionManager",
	basePackages = {"com.team33.moduleevent"}
)
@Configuration
public class EventDatasourceConfig {


	@Bean(name = "eventTransactionManager")
	public PlatformTransactionManager eventTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(eventEntityManagerFactory().getObject());
		return jpaTransactionManager;
	}

	@Bean(name = "eventEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean eventEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(eventDataSource());
		em.setPackagesToScan("com.team33.moduleevent");

		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(true);
		em.setJpaVendorAdapter(jpaVendorAdapter);

		return em;
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.main")
	public DataSource eventDataSource() {
		return DataSourceBuilder.create().build();
	}
}
