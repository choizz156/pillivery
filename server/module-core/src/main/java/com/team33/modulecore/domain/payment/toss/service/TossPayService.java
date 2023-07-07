package com.team33.modulecore.domain.payment.toss.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.team33.modulecore.domain.payment.toss.dto.TossApproveDto;
import com.team33.modulecore.global.util.JsonMapper;

@RequiredArgsConstructor
@Service
public class TossPayService {

    private final RestTemplate restTemplate;
    private static final String TOSS_REQUEST_URL = "https://api.tosspayments.com/v1/payments/confirm";

    public String approve(String paymentKey, String orderId, int amount) {
        String body = getBody(paymentKey, orderId, amount);

        HttpEntity<String> generalRequestEntity = new HttpEntity<>(body, getHeader());

        return restTemplate.postForObject(
                                            TOSS_REQUEST_URL,
                                            generalRequestEntity,
                                            String.class
        );
    }

    private String getBody(String paymentKey, String orderId, int amount) {
        TossApproveDto tossRequestDto = TossApproveDto.builder()
            .paymentKey(paymentKey)
            .orderId(orderId)
            .amount(amount)
            .build();

        return JsonMapper.objToString(tossRequestDto);
    }

    public HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization",
            "Basic dGVzdF9za19rWkxLR1B4NE0zTXFlMkpQTnkyM0JhV3lwdjFvOg==");
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}
