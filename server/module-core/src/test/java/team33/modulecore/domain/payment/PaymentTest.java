package team33.modulecore.domain.payment;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import team33.modulecore.domain.order.entity.Order;
import team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto.Approve;
import team33.modulecore.domain.payment.kakao.dto.KakaoResponseDto.Request;
import team33.modulecore.domain.payment.kakao.service.KaKaoPayApprove;
import team33.modulecore.domain.payment.kakao.service.KaKaoPayRequest;
import team33.modulecore.domain.payment.kakao.utils.ParameterProvider;

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
        BDDMockito.given(payRequest.requestOneTime(ArgumentMatchers.any(Order.class)))
            .willReturn(Request.class.getDeclaredConstructor().newInstance());

        //when
        Request request = payRequest.requestOneTime(order);

        //then
        assertThat(request).isInstanceOf(Request.class);
        Mockito.verify(parameterProvider, Mockito.times(1)).getOneTimeReqsParams(
            ArgumentMatchers.any(Order.class));
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.eq(Request.class));
    }

    @DisplayName("카카오 단건 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void payProveTest() throws Exception {
        //given
        Request request = fixtureMonkey.giveMeOne(Request.class);
        BDDMockito.given(payApprove.approveOneTime(
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong()))
            .willReturn(Approve.class.getDeclaredConstructor().newInstance());

        //when
        Approve approve = payApprove.approveOneTime(request.getTid(), "pgtoken", 1L);

        //then
        assertThat(approve).isInstanceOf(Approve.class);
        Mockito.verify(parameterProvider, Mockito.times(1))
            .getOneTimeApproveParams(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.eq(Approve.class));
    }

    @DisplayName("카카오 최초 정기 결제 요청이 완료되면 응답 객체를 받는다.")
    @Test
    void subFirstRequest() throws Exception {
        //given
        Order order = fixtureMonkey.giveMeOne(Order.class);
        BDDMockito.given(payRequest.requestSubscription(ArgumentMatchers.any(Order.class)))
            .willReturn(Request.class.getDeclaredConstructor().newInstance());

        //when
        Request request = payRequest.requestSubscription(order);

        //then
        assertThat(request).isInstanceOf(Request.class);
        Mockito.verify(parameterProvider, Mockito.times(1)).getSubscriptionReqsParams(
            ArgumentMatchers.any(Order.class));
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.eq(Request.class));
    }

    @DisplayName("카카오 최초 정기 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void subFirstApprove() throws Exception {
        //given
        BDDMockito.given(payApprove.approveFirstSubscription(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong()))
            .willReturn(Approve.class.getDeclaredConstructor().newInstance());

        //when
        Approve approve = payApprove.approveFirstSubscription("tid", "pgtoken", 1L);

        //then
        assertThat(approve).isInstanceOf(Approve.class);
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.eq(Approve.class));
    }

    @DisplayName("카카오 최초 정기 결제 후 결제 승인이 완료되면 응답 객체를 받는다.")
    @Test
    void subApprove() throws Exception {
        //given
        Order order = fixtureMonkey.giveMeOne(Order.class);
        BDDMockito.given(payApprove.approveSubscription(ArgumentMatchers.anyString(), ArgumentMatchers.any(Order.class)))
            .willReturn(Approve.class.getDeclaredConstructor().newInstance());
        //when
        Approve approve = payApprove.approveSubscription("sid", order);

        //then
        assertThat(approve).isInstanceOf(Approve.class);
        Mockito.verify(parameterProvider, Mockito.times(1))
            .getSubscriptionApproveParams(ArgumentMatchers.anyString(), ArgumentMatchers.any(Order.class));
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.eq(Approve.class));
    }
}

