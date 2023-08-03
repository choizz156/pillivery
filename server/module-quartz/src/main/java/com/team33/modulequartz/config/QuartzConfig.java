package com.team33.modulequartz.config;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

@Slf4j
@RequiredArgsConstructor
@Configuration
public class QuartzConfig {

    private DataSource dataSource;
    private PlatformTransactionManager transactionManager;

    public QuartzConfig(
        @Qualifier("schedulerDataSource") DataSource dataSource,
        @Qualifier("schedulerTransactionManager") PlatformTransactionManager transactionManager
    ) {
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
    }

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer() {
        return bean ->
        {
            bean.setDataSource(dataSource);
            bean.setTransactionManager(transactionManager);
            bean.setQuartzProperties(quartzProperties());
        };

    }

    @Bean
    @QuartzDataSource
    @ConfigurationProperties(prefix = "spring.datasource-quartz")
    public DataSource schedulerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public PlatformTransactionManager schedulerTransactionManager() {
        final DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(schedulerDataSource());

        return tm;
    }

    private Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("application-quartz.yml"));
        Properties properties = null;
        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
