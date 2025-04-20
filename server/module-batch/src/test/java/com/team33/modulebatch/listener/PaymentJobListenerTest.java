package com.team33.modulebatch.listener;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PaymentJobListenerTest {

    @DisplayName("beforeJob 메서드는 로깅을 수행한다")
    @Test
    void testBeforeJob() {
        // given
        PaymentJobListener listener = new PaymentJobListener();
        JobExecution jobExecution = createJobExecution();

        // when & then
        // 로깅만 수행하므로 예외가 발생하지 않는지만 확인
        assertThatCode(() -> listener.beforeJob(jobExecution))
                .doesNotThrowAnyException();
    }

    @DisplayName("afterJob 메서드는 로깅을 수행한다")
    @Test
    void testAfterJob() {
        // given
        PaymentJobListener listener = new PaymentJobListener();
        JobExecution jobExecution = createJobExecution();
        jobExecution.setStatus(BatchStatus.COMPLETED);

        // when & then
        // 로깅만 수행하므로 예외가 발생하지 않는지만 확인
        assertThatCode(() -> listener.afterJob(jobExecution))
                .doesNotThrowAnyException();
    }

    private JobExecution createJobExecution() {
        Map<String, JobParameter> params = new HashMap<>();
        params.put("paymentDate", new JobParameter(new Date()));
        JobParameters jobParameters = new JobParameters(params);

        return new JobExecution(1L, jobParameters);
    }
}
