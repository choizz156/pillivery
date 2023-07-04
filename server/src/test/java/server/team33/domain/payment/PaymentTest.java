package server.team33.domain.payment;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.navercorp.fixturemonkey.FixtureMonkey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import server.team33.domain.order.entity.Order;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto.Approve;
import server.team33.domain.payment.kakao.dto.KakaoResponseDto.Request;
import server.team33.domain.payment.kakao.service.KaKaoPayApprove;
import server.team33.domain.payment.kakao.service.KaKaoPayRequest;
import server.team33.domain.payment.kakao.utils.ParameterProvider;

@ExtendWith(MockitoExtension.class)
class PaymentTest {

    @InjectMocks
    private KaKaoPayRequest payRequest;

    @InjectMocks
    private KaKaoPayApprove payApprove;

    @Mock
    private ParameterProvider parameterProvider;

    @Mock
    private RestTemplate restTemplate;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey.create();

    @DisplayName("카카오 단건 결제 요청이 완료되면 응답 객체를 받는다.")
    @Test
    void payRequestTest() throws Exception {
        //then
        Order order = fixtureMonkey.giveMeOne(Order.class);
        given(payRequest.requestOneTime(any(Order.class)))
            .willReturn(Request.class.getDeclaredConstructor().newInstance());

        //when
        Request request = payRequest.requestOneTime(order);

        //then
        assertThat(request).isInstanceOf(Request.class);
        verify(parameterProvider, times(1)).getOneTimeReqsParams(any(Order.class));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(Request.class));
    }

    @DisplayName("카카오 단건 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void payProveTest() throws Exception {
        //given
        Request request = fixtureMonkey.giveMeOne(Request.class);
        given(payApprove.approveOneTime(anyString(), anyString(), anyLong()))
            .willReturn(Approve.class.getDeclaredConstructor().newInstance());

        //when
        Approve approve = payApprove.approveOneTime(request.getTid(), "pgtoken", 1L);

        //then
        assertThat(approve).isInstanceOf(Approve.class);
        verify(parameterProvider, times(1))
            .getOneTimeApproveParams(anyString(), anyString(), anyLong());
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(Approve.class));
    }

    @DisplayName("카카오 최초 정기 결제 요청이 완료되면 응답 객체를 받는다.")
    @Test
    void subFirstRequest() throws Exception {
        //given
        Order order = fixtureMonkey.giveMeOne(Order.class);
        given(payRequest.requestSubscription(any(Order.class)))
            .willReturn(Request.class.getDeclaredConstructor().newInstance());

        //when
        Request request = payRequest.requestSubscription(order);

        //then
        assertThat(request).isInstanceOf(Request.class);
        verify(parameterProvider, times(1)).getSubscriptionReqsParams(any(Order.class));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(Request.class));
    }

    @DisplayName("카카오 최초 정기 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void subFirstApprove() throws Exception {
        //given
        given(payApprove.approveFirstSubscription(anyString(), anyString(), anyLong()))
            .willReturn(Approve.class.getDeclaredConstructor().newInstance());

        //when
        Approve approve = payApprove.approveFirstSubscription("tid", "pgtoken", 1L);

        //then
        assertThat(approve).isInstanceOf(Approve.class);
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(Approve.class));
    }

    @DisplayName("카카오 최초 정기 결제 후 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void subApprove() throws Exception {
        //given
        Order order = fixtureMonkey.giveMeOne(Order.class);
        given(payApprove.approveSubscription(anyString(), any(Order.class)))
            .willReturn(Approve.class.getDeclaredConstructor().newInstance());
        //when
        Approve approve = payApprove.approveSubscription("sid", order);

        //then
        assertThat(approve).isInstanceOf(Approve.class);
        verify(parameterProvider, times(1))
            .getSubscriptionApproveParams(anyString(), any(Order.class));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(Approve.class));
    }
}

