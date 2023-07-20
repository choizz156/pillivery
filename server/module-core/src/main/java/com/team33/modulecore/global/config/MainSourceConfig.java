package com.team33.modulecore.global.config;

import static org.springframework.orm.jpa.vendor.Database.MYSQL;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;


@EnableJpaRepositories(
    basePackages = "com.team33.modulecore.domain",
    entityManagerFactoryRef = "mainEntityManager",
    transactionManagerRef = "mainTransactionManager"
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
        em.setPackagesToScan("com.team33.modulecore.domain");
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(MYSQL);
        em.setJpaVendorAdapter(jpaVendorAdapter);
        return em;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-main")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create().build();
    }
}
