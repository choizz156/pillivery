package com.team33.modulebatch.listener;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

@ExtendWith(MockitoExtension.class)
class PaymentStepExecutionListenerTest {

    @DisplayName("Step이 실패하면 실패 상태를 반환한다")
    @Test
    void test1() {
        // given
        PaymentStepExecutionListener listener = new PaymentStepExecutionListener();
        JobExecution jobExecution = new JobExecution(1L);
        StepExecution stepExecution = new StepExecution("testStep", jobExecution);
        
        stepExecution.setStatus(BatchStatus.FAILED);
        stepExecution.addFailureException(new RuntimeException("테스트 예외"));
        stepExecution.setExitStatus(ExitStatus.FAILED);

        // when
        ExitStatus exitStatus = listener.afterStep(stepExecution);

        // then
        assertThat(exitStatus).isEqualTo(ExitStatus.FAILED);

    }

    @DisplayName("Step이 성공하면 성공 상태를 반환한다")
    @Test
    void test2() {
        // given
        PaymentStepExecutionListener listener = new PaymentStepExecutionListener();
        JobExecution jobExecution = new JobExecution(1L);
        StepExecution stepExecution = new StepExecution("testStep", jobExecution);
        
        stepExecution.setStatus(BatchStatus.COMPLETED);
        stepExecution.setExitStatus(ExitStatus.COMPLETED);

        // when
        ExitStatus exitStatus = listener.afterStep(stepExecution);

        // then
        assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
    }
}