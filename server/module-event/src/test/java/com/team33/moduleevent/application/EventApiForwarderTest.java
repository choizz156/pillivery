package com.team33.moduleevent.application;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.repository.EventRepository;

class EventApiForwarderTest {

    private EventRepository eventRepository;
    private EventDispatcher eventDispatcher;
    private EventSender kakaoCancelEventSender;
    private EventSender subscriptionRegisterEventSender;
    private EventApiForwarder eventApiForwarder;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventDispatcher = mock(EventDispatcher.class);
        kakaoCancelEventSender = mock(EventSender.class);
        subscriptionRegisterEventSender = mock(EventSender.class);

        eventApiForwarder = new EventApiForwarder(
            eventRepository,
            eventDispatcher,
            kakaoCancelEventSender,
            subscriptionRegisterEventSender
        );
    }

    @DisplayName("등록된 이벤트가 없는 경우 아무 일도 일어나지 않는다")
    @Test
    void test1() {
        // given
        when(eventRepository.findTop20ByStatusOrderByCreatedAt(EventStatus.READY))
            .thenReturn(Collections.emptyList());

        // when
        eventApiForwarder.fetchAndForwardEvents();

        // then
        verify(eventRepository).findTop20ByStatusOrderByCreatedAt(EventStatus.READY);
        verifyNoInteractions(eventDispatcher);
    }

    @DisplayName("KAKAO_REFUNDED 이벤트는 카카오 취소 이벤트 발송자에게 전달된다")
    @Test
    void test2() {
        // given
        ApiEvent kakaoRefundedEvent = createApiEvent(EventType.KAKAO_REFUNDED);

        when(eventRepository.findTop20ByStatusOrderByCreatedAt(EventStatus.READY))
            .thenReturn(List.of(kakaoRefundedEvent));

        // when
        eventApiForwarder.fetchAndForwardEvents();

        // then
        verify(eventRepository).findTop20ByStatusOrderByCreatedAt(EventStatus.READY);
        verify(eventDispatcher).register(kakaoRefundedEvent, kakaoCancelEventSender);
    }

    @DisplayName("SUBSCRIPTION_CANCELED 이벤트는 카카오 취소 이벤트 발송자에게 전달된다")
    @Test
    void test3() {
        // given
        ApiEvent subscriptionCanceledEvent = createApiEvent(EventType.SUBSCRIPTION_CANCELED);

        when(eventRepository.findTop20ByStatusOrderByCreatedAt(EventStatus.READY))
            .thenReturn(List.of(subscriptionCanceledEvent));

        // when
        eventApiForwarder.fetchAndForwardEvents();

        // then
        verify(eventRepository).findTop20ByStatusOrderByCreatedAt(EventStatus.READY);
        verify(eventDispatcher).register(subscriptionCanceledEvent, kakaoCancelEventSender);
    }

    @DisplayName("SUBSCRIPTION_REGISTERED 이벤트는 구독 등록 이벤트 발송자에게 전달된다")
    @Test
    void test4() {
        // given
        ApiEvent subscriptionRegisteredEvent = createApiEvent(EventType.SUBSCRIPTION_REGISTERED);

        when(eventRepository.findTop20ByStatusOrderByCreatedAt(EventStatus.READY))
            .thenReturn(List.of(subscriptionRegisteredEvent));

        // when
        eventApiForwarder.fetchAndForwardEvents();

        // then
        verify(eventRepository).findTop20ByStatusOrderByCreatedAt(EventStatus.READY);
        verify(eventDispatcher).register(subscriptionRegisteredEvent, subscriptionRegisterEventSender);
    }
    
   
    private ApiEvent createApiEvent(EventType eventType) {
        return ApiEvent.builder()
            .type(eventType)
            .contentType("application/json")
            .parameters("params")
            .url("url")
            .status(EventStatus.READY)
            .createdAt(LocalDateTime.now())
            .build();
    }
}