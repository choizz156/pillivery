package com.team33.modulebatch.scheduler.job;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

@ExtendWith(MockitoExtension.class)
class PaymentScheduleJobTest {

    @Mock
    private Job paymentJob;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @InjectMocks
    private PaymentScheduleJob paymentScheduleJob;

    @DisplayName("Job을 실행한다")
    @Test
    void testExecuteInternal() throws Exception {
        // when
        paymentScheduleJob.executeInternal(jobExecutionContext);

        // then
        verify(jobLauncher).run(eq(paymentJob), any(JobParameters.class));
    }

    @DisplayName("Job 실행 중 예외가 발생하면 RuntimeException으로 감싸서 던진다")
    @Test
    void testExecuteInternalWithException() throws Exception {
        // given
        doThrow(new JobExecutionAlreadyRunningException("Test exception"))
            .when(jobLauncher).run(any(), any());

        // when & then
        assertThatThrownBy(() -> paymentScheduleJob.executeInternal(jobExecutionContext))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(JobExecutionAlreadyRunningException.class);
    }

    @DisplayName("JobRestartException 발생 시 RuntimeException으로 감싸서 던진다")
    @Test
    void testExecuteInternalWithJobRestartException() throws Exception {
        // given
        doThrow(new JobRestartException("Test exception"))
            .when(jobLauncher).run(any(), any());

        // when & then
        assertThatThrownBy(() -> paymentScheduleJob.executeInternal(jobExecutionContext))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(JobRestartException.class);
    }

    @DisplayName("JobInstanceAlreadyCompleteException 발생 시 RuntimeException으로 감싸서 던진다")
    @Test
    void testExecuteInternalWithJobInstanceAlreadyCompleteException() throws Exception {
        // given
        doThrow(new JobInstanceAlreadyCompleteException("Test exception"))
            .when(jobLauncher).run(any(), any());

        // when & then
        assertThatThrownBy(() -> paymentScheduleJob.executeInternal(jobExecutionContext))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(JobInstanceAlreadyCompleteException.class);
    }

    @DisplayName("JobParametersInvalidException 발생 시 RuntimeException으로 감싸서 던진다")
    @Test
    void testExecuteInternalWithJobParametersInvalidException() throws Exception {
        // given
        doThrow(new JobParametersInvalidException("Test exception"))
            .when(jobLauncher).run(any(), any());

        // when & then
        assertThatThrownBy(() -> paymentScheduleJob.executeInternal(jobExecutionContext))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(JobParametersInvalidException.class);
    }
}