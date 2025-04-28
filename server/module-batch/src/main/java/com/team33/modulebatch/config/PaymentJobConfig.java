package com.team33.modulebatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.team33.modulebatch.listener.PaymentJobListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class PaymentJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final Step paymentJobStep;

	@Bean
	public Job paymentJob(){
		return jobBuilderFactory.get("paymentJob")
			.listener(new PaymentJobListener())
			.start(paymentJobStep)
			.build();
	}
}
