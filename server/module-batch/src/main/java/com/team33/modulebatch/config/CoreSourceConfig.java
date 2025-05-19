package com.team33.modulebatch.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("!test")
@EnableJpaRepositories(
	entityManagerFactoryRef = "mainEntityManagerFactory",
	transactionManagerRef = "mainTransactionManager",
	basePackages = {
		"com.team33.modulecore.core.order.domain",
		"com.team33.modulecore.core.item.domain",
		"com.team33.modulebatch",
	}
)
@EntityScan(basePackages = {
	"com.team33.modulebatch.domain",
	"com.team33.modulecore.core.order.domain",
	"com.team33.modulecore.core.item.domain"
})
@Configuration
public class CoreSourceConfig {

	private final Environment env;

	@Bean(name = "mainTransactionManager")
	public PlatformTransactionManager mainTransactionManager() {

		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(mainEntityManagerFactory().getObject());
		return jpaTransactionManager;
	}

	@Bean(name = "mainEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory() {

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(mainDataSource());
		em.setPackagesToScan(
			"com.team33.modulecore.core.order.domain",
			"com.team33.modulecore.core.item.domain",
			"com.team33.modulebatch");
		em.setJpaProperties(getJpaProperties());

		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(true);
		em.setJpaVendorAdapter(jpaVendorAdapter);

		return em;
	}

	@Bean(name = "mainDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.core")
	public DataSource mainDataSource() {

		return DataSourceBuilder.create().build();
	}

	private Properties getJpaProperties() {

		Properties properties = new Properties();
		properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
		properties.put("hibernate.physical_naming_strategy",
			env.getProperty("spring.jpa.properties.hibernate.physical_naming_strategy"));
		properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
		// properties.put("hibernate.show_sql", env.getProperty("spring.jpa.properties.hibernate.show_sql"));
		properties.put("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
		properties.put("hibernate.jdbc.batch_size", env.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size"));
		properties.put("hibernate.order_inserts", env.getProperty("spring.jpa.properties.hibernate.order_inserts"));

		return properties;
	}
}
