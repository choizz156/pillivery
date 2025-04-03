package com.team33.moduleevent.handler;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulecore.core.payment.kakao.application.events.KakaoSubsCanceledEvent;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
class SubscriptionCanceledEventHandlerTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private SubscriptionCanceledEventHandler handler;

    @DisplayName("구독 취소 이벤트를 저장할 수 있다.")
    @Test
    void test1() {
        // given
        String cancelParam = "param";
        String cancelUrl = "url";
        KakaoSubsCanceledEvent event = new KakaoSubsCanceledEvent(cancelParam, cancelUrl);
        
        when(eventRepository.findByTypeAndParameters(EventType.SUBSCRIPTION_CANCELED, cancelParam))
            .thenReturn(Optional.empty());

        // when
        handler.onEventSet(event);

        // then
        verify(eventRepository).save(any(ApiEvent.class));
    }

    @DisplayName("동시에 같은 구독 취소 이벤트가 발생해도 한번만 처리되어야 한다")
    @Test
    void test3() throws InterruptedException {
        // given
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        String cancelParam = "test-param";
        String cancelUrl = "test-url";
        KakaoSubsCanceledEvent event = new KakaoSubsCanceledEvent(cancelParam, cancelUrl);

		ApiEvent testEvent = ApiEvent.builder().url("url").build();
		when(eventRepository.findByTypeAndParameters(EventType.SUBSCRIPTION_CANCELED, cancelParam))
            .thenReturn(Optional.empty())
			.thenReturn(Optional.of(testEvent))
			.thenReturn(Optional.of(testEvent));


        // when
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    handler.onEventSet(event);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        // then
        verify(eventRepository, times(1)).save(any(ApiEvent.class));
    }
}