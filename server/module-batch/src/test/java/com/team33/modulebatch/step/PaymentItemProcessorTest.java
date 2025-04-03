package com.team33.modulebatch.step;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentItemProcessorTest {

    @DisplayName("jobId가 설정되지 않으면 예외가 발생한다")
    @Test
    void test1() {
        // given
        PaymentItemProcessor processor = new PaymentItemProcessor();
        SubscriptionOrderVO orderVO = new SubscriptionOrderVO();

        // when then
        assertThatThrownBy(() -> processor.process(orderVO))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("JobId가 설정돼있어야 합니다.");
    }

    @DisplayName("주문 정보로부터 idempotencyKey를 생성한다")
    @Test
    void test2() {
        // given
        PaymentItemProcessor processor = new PaymentItemProcessor();
        processor.initialize(1000L);

        SubscriptionOrderVO subscriptionOrderVO = new SubscriptionOrderVO();
        subscriptionOrderVO.setSubscriptionOrderId(500L);
        LocalDate nextPaymentDate = LocalDateTime.of(2024, 1, 1, 0, 0).toLocalDate();
        subscriptionOrderVO.setNextPaymentDate(nextPaymentDate);

        // when
        SubscriptionOrderVO processed = processor.process(subscriptionOrderVO);

        // then
        String expectedKey = "1000_500_" + nextPaymentDate;
        assertThat(processed.getIdempotencyKey()).isEqualTo(expectedKey);
    }
}