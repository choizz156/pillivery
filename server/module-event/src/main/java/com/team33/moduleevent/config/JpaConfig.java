package com.team33.moduleevent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(
	entityManagerFactoryRef = "mainEntityManager",
	transactionManagerRef = "mainTransactionManager",
	basePackages = {"com.team33.moduleevent"}
)
@Configuration
public class JpaConfig {
}
