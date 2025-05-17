package com.team33.modulebatch;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulebatch.config.job.PaymentJobConfig;
import com.team33.modulebatch.config.step.PaymentStepConfig;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulebatch.infra.RestTemplateSender;
import com.team33.modulebatch.step.PaymentItemProcessor;
import com.team33.modulebatch.step.PaymentWriter;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.repository.SubscriptionOrderRepository;

import io.restassured.RestAssured;

@SpringBootTest(classes = {
	PaymentStepConfig.class,
	PaymentJobConfig.class,
	PaymentApiDispatcher.class,
	PaymentWriter.class,
	PaymentItemProcessor.class,
	SubscriptionOrderService.class,
	SubscriptionOrderRepository.class,
	DataCleaner.class,
	TestDataSourceConfig.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBatchTest
@EnableAutoConfiguration
@EnableBatchProcessing
@EntityScan(basePackages = {"com.team33.modulebatch.domain", "com.team33.modulecore"})
@EnableJpaRepositories(basePackages = {"com.team33.modulebatch.domain", "com.team33.modulecore"})
@ActiveProfiles("test")
public abstract class BatchApiTest {

	@LocalServerPort
	private int port;
	@Autowired
	protected StepBuilderFactory stepBuilderFactory;
	@Autowired
	protected JobRepository jobRepository;
	@Autowired
	protected PaymentItemProcessor paymentItemProcessor;
	@MockBean
	protected RestTemplateSender restTemplateSender;
	@Autowired
	protected DataCleaner dataCleaner;

	@BeforeEach
	void beforeEach() throws Exception {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
			dataCleaner.afterPropertiesSet();
		}
	}

	@AfterEach
	void tearDown() {
		dataCleaner.execute();
	}
}