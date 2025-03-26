package com.team33.modulebatch;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulebatch.config.PaymentJobConfig;
import com.team33.modulebatch.config.PaymentStepConfig;
import com.team33.modulebatch.infra.PaymentApiDispatcher;
import com.team33.modulecore.config.redis.EmbededRedisConfig;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import io.restassured.RestAssured;

@SpringBootTest(classes = {
	PaymentStepConfig.class,
	PaymentJobConfig.class,
	PaymentApiDispatcher.class,
	EmbededRedisConfig.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBatchTest
@EnableAutoConfiguration
@EnableBatchProcessing
@ActiveProfiles("test")
public abstract class BatchApiTest {

	@LocalServerPort
	private int port;

	@Autowired
	protected StepBuilderFactory stepBuilderFactory;

	@Autowired
	protected JobRepository jobRepository;

	@Autowired
	protected PaymentApiDispatcher paymentApiDispatcher;

	@MockBean
	protected RestTemplateSender restTemplateSender;

	@BeforeEach
	void beforeEach() throws Exception {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
	}
}