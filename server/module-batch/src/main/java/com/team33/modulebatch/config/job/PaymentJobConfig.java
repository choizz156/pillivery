package com.team33.modulebatch.config.job;

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
	private final Step PaymentRetry1Step;
	private final Step PaymentRetry2Step;
	private final Step PaymentRetry3Step;

	@Bean
	public Job paymentJob(){
		return jobBuilderFactory.get("paymentJob")
			.listener(new PaymentJobListener())
			.start(paymentJobStep)
			.next(PaymentRetry1Step)
			.next(PaymentRetry2Step)
			.next(PaymentRetry3Step)
			.build();
	}
}
