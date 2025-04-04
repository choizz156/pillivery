package com.team33.moduleevent.handler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.team33.modulecore.core.payment.kakao.application.events.KakaoRefundedEvent;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;
import com.team33.moduleredis.aop.DistributedLockAspect;
import com.team33.moduleredis.application.aspect.DistributedLockService;
import com.team33.moduleredis.config.EmbededRedisConfig;

@SpringJUnitConfig(classes = {
	RefundEventHandler.class,
	DistributedLockAspect.class,
	DistributedLockService.class,
	EmbededRedisConfig.class,
	AopAutoConfiguration.class
})
@EnableAspectJAutoProxy
@ActiveProfiles("test")
class RefundEventHandlerTest {

	@Autowired
	private RefundEventHandler refundEventHandler;

	@MockBean
	private EventRepository eventRepository;

	@MockBean
	private RedissonClient redissonClient;

	@MockBean
	private RLock rLock;

	@BeforeEach
	void setUp() throws Exception {
		when(redissonClient.getLock(anyString())).thenReturn(rLock);
		when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
	}

	@DisplayName("환불 이벤트를 저장할 수 있다.")
	@Test
	void onEventSet() {
		// given
		when(eventRepository.save(any(ApiEvent.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		RefundEventHandler refundEventHandler = new RefundEventHandler(eventRepository);

		// when
		refundEventHandler.onEventSet(new KakaoRefundedEvent("refundParams", "refundUrl"));

		// then
		verify(eventRepository, times(1)).save(any(ApiEvent.class));
	}

	@Test
	@DisplayName("동시에 같은 환불 이벤트가 들어올 경우 하나만 처리되어야 한다")
	void concurrentRefundEventTest() throws InterruptedException {
		// given
		int threadCount = 3;
		CountDownLatch latch = new CountDownLatch(threadCount);

		KakaoRefundedEvent event = new KakaoRefundedEvent("params", "test-url");

		ApiEvent testEvent = ApiEvent.builder().url("11").build();

		when(eventRepository.findByTypeAndParameters(any(EventType.class), anyString()))
			.thenReturn(Optional.empty())
			.thenReturn(Optional.of(testEvent))
			.thenReturn(Optional.of(testEvent));

		when(eventRepository.save(any(ApiEvent.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				try {
					refundEventHandler.onEventSet(event);
				} catch (Exception ignored) {
				} finally {
					latch.countDown();
				}
			}).start();
		}

		latch.await();

		// then
		verify(eventRepository, times(3)).findByTypeAndParameters(any(EventType.class), anyString());
		verify(eventRepository, times(1)).save(any(ApiEvent.class));
	}

	@Test
	@DisplayName("중복된 환불 이벤트는 예외가 발생해야 한다")
	void test3() {
		// Given
		KakaoRefundedEvent event = new KakaoRefundedEvent("params", "test-url");

		when(eventRepository.findByTypeAndParameters(any(), any()))
			.thenReturn(Optional.of(ApiEvent.builder().build()));

		// When & Then
		assertThatNoException().isThrownBy(() -> refundEventHandler.onEventSet(event));
	}
}