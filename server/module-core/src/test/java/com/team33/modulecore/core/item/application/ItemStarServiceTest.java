package com.team33.modulecore.core.item.application;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.team33.modulecore.aop.DistributedLockAop;
import com.team33.modulecore.config.redis.RedisTestConfig;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Statistic;
import com.team33.modulecore.core.item.domain.entity.Item;

@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {RedisTestConfig.class, ItemStarService.class, Item.class, DistributedLockAop.class})
@SpringBootTest
public class ItemStarServiceTest {

	@Autowired
	private ItemStarService itemStarService;

	@DisplayName("멀티스레드 상황에서 lock을 이용해 별점을 정확하게 계산할 수 있다.")
	@Test
	void 별점_테스트() throws Exception {
		//given
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch countDownLatch = new CountDownLatch(10);

		Item item = Item.builder()
			.id(1L)
			.statistics(new Statistic())
			.information(Information.builder().productName("test").build())
			.build();

		//when
		for (int i = 0; i < 10; i++) {
			double starAvg = Math.min(i, 5.0);
			executorService.submit(() -> {
				try {
					itemStarService.updateStarAvg(item, starAvg);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		assertThat(Math.round(item.getStarAvg() * 100.0) / 100.0).isEqualTo(3.50);
	}
}