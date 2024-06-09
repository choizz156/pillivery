package com.team33.modulecore.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Profile("prod")
@EnableJpaRepositories(
    entityManagerFactoryRef = "mainEntityManager",
    transactionManagerRef = "mainTransactionManager",
    basePackages = "com.team33.modulecore"
)
@Configuration
public class MainSourceConfig {

    @Primary
    @Bean
    public PlatformTransactionManager mainTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(mainEntityManager().getObject());
        return jpaTransactionManager;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean mainEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource());
        em.setPackagesToScan("com.team33.modulecore");
        em.setJpaProperties(getJpaProperties());

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(true);
        em.setJpaVendorAdapter(jpaVendorAdapter);
        return em;
    }

    private Properties getJpaProperties() {

        Properties properties = new Properties();

        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");

        return properties;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-main")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create().url("jdbc:h2:tcp://localhost/~/test").username("sa").password("").build();
    }
}
