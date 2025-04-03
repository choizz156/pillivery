package com.team33.moduleevent.infra;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleexternalapi.infra.WebClientSender;

@ExtendWith(MockitoExtension.class)
class SubscriptionRegisterEventSenderTest {

    @Mock
    private WebClientSender webClientSender;
    private SubscriptionRegisterEventSender subscriptionRegisterEventSender;

    @BeforeEach
    void setUp() {
        subscriptionRegisterEventSender = new SubscriptionRegisterEventSender(webClientSender);
    }

    @DisplayName("구독 등록 요청을 WebClientSender에 위임한다")
    @Test
    void test1() throws JsonProcessingException {
        // given
        String parameters = "101";
        String url = "url";
        
        ApiEvent apiEvent = ApiEvent.builder()
            .parameters(parameters)
            .url(url)
            .build();
            
        when(webClientSender.sendToPostSync(anyMap(), eq(url + parameters), eq(null), eq(String.class)))
            .thenReturn("success");

        // when
        subscriptionRegisterEventSender.send(apiEvent);

        // then
        verify(webClientSender, times(1)).sendToPostSync(anyMap(), eq(url + parameters), eq(null), eq(String.class));
    }
}