package com.team33.modulebatch.config.step;

import java.time.LocalDate;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.team33.modulebatch.domain.DelayedItemRepository;
import com.team33.modulebatch.domain.ErrorStatus;
import com.team33.modulebatch.domain.entity.DelayedItem;
import com.team33.modulebatch.listener.RetryStepListener;
import com.team33.modulebatch.step.RetryItemProcessor;

@Configuration
public class RetryStepConfig3 {

	private final static int CHUNK_SIZE = 20;

	@Autowired
	private EntityManagerFactory mainEntityManagerFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private RetryItemProcessor retryItemProcessor;

	@Autowired
	private DelayedItemRepository delayedItemRepository;

	@Bean
	public Step PaymentRetry3Step() {

		return stepBuilderFactory.get("paymentRetry3Step")
			.<DelayedItem, DelayedItem>chunk(CHUNK_SIZE)
			.reader(delayedItemReader3())
			.processor(delayedItemProcessor3(null))
			.writer(delayedItemWriter3())
			.listener(new RetryStepListener())
			.faultTolerant()
			.build();
	}

	@Bean
	public ItemReader<DelayedItem> delayedItemReader3() {
		int retryCount = 5;
		LocalDate targetDate = LocalDate.now().minusDays(retryCount);
		int expectedRetryCount = retryCount - 1;

		return new JpaPagingItemReaderBuilder<DelayedItem>()
			.name("delayedItemReader")
			.entityManagerFactory(mainEntityManagerFactory)
			.queryString("SELECT d FROM DelayedItem d " +
				"WHERE d.delayedPaymentDate = :date " +
				"AND d.retryCount = :count")
			.parameterValues(Map.of(
				"date", targetDate,
				"count", expectedRetryCount
			))
			.pageSize(CHUNK_SIZE)
			.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<DelayedItem, DelayedItem> delayedItemProcessor3(
		@Value("#{jobParameters['jobId']}") Long jobId
	) {

		retryItemProcessor.initialize(jobId);
		return retryItemProcessor;
	}

	@Bean
	public ItemWriter<DelayedItem> delayedItemWriter3() {

		return items -> items.stream()
			.filter(item -> item.getStatus() == ErrorStatus.DELAYED)
			.forEach(
				delayedItem -> {
					delayedItem.updateStatus(ErrorStatus.CANCELLED);
					delayedItemRepository.save(delayedItem);
				}
			);
	}

}

