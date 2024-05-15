package com.team33.moduleapi.controller.payment;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.payment.kakao.dto.KaKaoResponseApproveDto;
import com.team33.modulecore.payment.kakao.dto.KaKaoPayRequestDto;
import com.team33.modulecore.payment.kakao.application.KaKaoPayApproveService;
import com.team33.modulecore.payment.kakao.application.KaKaoPayRequestService;
import com.team33.modulecore.payment.kakao.infra.ParameterProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class PaymentTest {

    @InjectMocks
    private KaKaoPayRequestService payRequest;

    @InjectMocks
    private KaKaoPayApproveService payApprove;

    @Mock
    private ParameterProvider parameterProvider;

    @Mock
    private RestTemplate restTemplate;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .build();

    @DisplayName("카카오 단건 결제 요청이 완료되면 응답 객체를 받는다.")
    @Test
    void payRequestTest() throws Exception {
        //then
        Order order = fixtureMonkey.giveMeOne(Order.class);
        KaKaoPayRequestDto req = fixtureMonkey.giveMeOne(KaKaoPayRequestDto.class);
        given(payRequest.requestOneTime(any(Order.class)))
            .willReturn(req);

        //when
        KaKaoPayRequestDto request = payRequest.requestOneTime(order);

        //then
        assertThat(request).isInstanceOf(KaKaoPayRequestDto.class);
        verify(parameterProvider, times(1)).getOneTimeReqsParams(
            any(Order.class));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(KaKaoPayRequestDto.class));
    }

    @DisplayName("카카오 단건 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void payProveTest() throws Exception {
        //given
        KaKaoResponseApproveDto approve1 = fixtureMonkey.giveMeOne(KaKaoResponseApproveDto.class);
        given(payApprove.approveOneTime(anyString(), anyString(), anyLong()))
            .willReturn(approve1);

        //when
        KaKaoResponseApproveDto approve = payApprove.approveOneTime("tid", "pgtoken", 1L);

        //then
        assertThat(approve).isInstanceOf(KaKaoResponseApproveDto.class);
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(KaKaoResponseApproveDto.class));
    }

    @DisplayName("카카오 최초 정기 결제 요청이 완료되면 응답 객체를 받는다.")
    @Test
    void subFirstRequest() throws Exception {
        //given
        KaKaoPayRequestDto request1 = fixtureMonkey.giveMeOne(KaKaoPayRequestDto.class);
        Order order = fixtureMonkey.giveMeOne(Order.class);
        given(payRequest.requestSubscription(any(Order.class)))
            .willReturn(request1);

        //when
        KaKaoPayRequestDto request = payRequest.requestSubscription(order);

        //then
        assertThat(request).isInstanceOf(KaKaoPayRequestDto.class);
        verify(parameterProvider, times(1)).getSubscriptionReqsParams(
            any(Order.class));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(KaKaoPayRequestDto.class));
    }

    @DisplayName("카카오 최초 정기 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void subFirstApprove() throws Exception {
        //given
        KaKaoResponseApproveDto approve1 = fixtureMonkey.giveMeOne(KaKaoResponseApproveDto.class);
        given(payApprove.approveFirstSubscription(anyString(), anyString(), anyLong()))
            .willReturn(approve1);

        //when
        KaKaoResponseApproveDto approve = payApprove.approveFirstSubscription("tid", "pgtoken", 1L);

        //then
        assertThat(approve).isInstanceOf(KaKaoResponseApproveDto.class);
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(KaKaoResponseApproveDto.class));
    }

    @DisplayName("카카오 최초 정기 결제 후 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void subApprove() throws Exception {
        //given
        KaKaoResponseApproveDto approve1 = fixtureMonkey.giveMeOne(KaKaoResponseApproveDto.class);
        Order order = fixtureMonkey.giveMeOne(Order.class);
        given(payApprove.approveSubscription(anyString(), any(Order.class)))
            .willReturn(approve1);
        //when
        KaKaoResponseApproveDto approve = payApprove.approveSubscription("sid", order);

        //then
        assertThat(approve).isInstanceOf(KaKaoResponseApproveDto.class);
        verify(parameterProvider, times(1))
            .getSubscriptionApproveParams(anyString(), any(Order.class));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(KaKaoResponseApproveDto.class));
    }
}

