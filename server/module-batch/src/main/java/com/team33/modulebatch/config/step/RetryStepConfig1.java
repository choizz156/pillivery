package com.team33.modulebatch.config.step;

import java.time.LocalDate;
import java.time.ZoneId;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.team33.modulebatch.domain.DelayedItemRepository;
import com.team33.modulebatch.domain.ErrorStatus;
import com.team33.modulebatch.domain.entity.DelayedItem;
import com.team33.modulebatch.listener.RetryStepListener;
import com.team33.modulebatch.step.RetryItemProcessor;

@Configuration
public class RetryStepConfig1 {

	private final static int CHUNK_SIZE = 20;

	@Autowired
	@Qualifier("mainEntityManagerFactory")
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private RetryItemProcessor retryItemProcessor;

	@Autowired
	private DelayedItemRepository delayedItemRepository;

	@Bean
	public Step PaymentRetry1Step() {

		return stepBuilderFactory.get("paymentRetry1Step")
			.<DelayedItem, DelayedItem>chunk(CHUNK_SIZE)
			.reader(delayedItemReader())
			.processor(delayedItemProcessor(null))
			.writer(delayedItemWriter())
			.listener(new RetryStepListener())
			.faultTolerant()
			.build();
	}

	@Bean(name = "delayedItemReader")
	public ItemReader<DelayedItem> delayedItemReader() {

		long retryCount = 1L;
		LocalDate targetDate = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(retryCount);
		long expectedRetryCount = 0L;

		return new JpaPagingItemReaderBuilder<DelayedItem>()
			.name("delayedItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT d FROM DelayedItem d " +
				"WHERE d.originalPaymentDate = :date " +
				"AND d.retryCount = :count")
			.parameterValues(Map.of(
				"date", targetDate,
				"count", expectedRetryCount
			))
			.pageSize(CHUNK_SIZE)
			.build();
	}

	@Bean(name = "delayedItemProcessor")
	@StepScope
	public ItemProcessor<DelayedItem, DelayedItem> delayedItemProcessor(
		@Value("#{jobParameters['jobId']}") Long jobId
	) {

		retryItemProcessor.initialize(jobId);
		return retryItemProcessor;
	}

	@Bean(name = "delayedItemWriter")
	public ItemWriter<DelayedItem> delayedItemWriter() {

		return items -> items
			.forEach(
				delayedItem -> {
					if (delayedItem.getStatus() == ErrorStatus.DELAYED) {
						delayedItem.addRetryCount(delayedItem.getRetryCount() + 1);
					}
					delayedItemRepository.save(delayedItem);
				}
			);
	}

}

