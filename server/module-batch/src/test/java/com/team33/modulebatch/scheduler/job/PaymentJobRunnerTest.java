package com.team33.modulebatch.scheduler.job;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.team33.modulebatch.scheduler.application.JobDetailService;
import com.team33.modulebatch.scheduler.application.TriggerService;

@ExtendWith(MockitoExtension.class)
class PaymentJobRunnerTest {

    @Mock
    private Scheduler scheduler;

    @Mock
    private JobDetailService jobDetailService;

    @Mock
    private TriggerService triggerService;

    @InjectMocks
    private PaymentJobRunner paymentJobRunner;

    @DisplayName("PaymentScheduleJob을 스케줄러에 등록한다")
    @Test
    void testRun() throws Exception {
        // given
        JobDetail mockJobDetail = mock(JobDetail.class);
        Trigger mockTrigger = mock(Trigger.class);

        when(jobDetailService.buildJobDetail(eq(PaymentScheduleJob.class), any())).thenReturn(mockJobDetail);
        when(triggerService.now()).thenReturn(mockTrigger);

        // when
        paymentJobRunner.run();

        // then
        verify(jobDetailService).buildJobDetail(eq(PaymentScheduleJob.class), any());
        verify(triggerService).now();
        verify(scheduler).scheduleJob(mockJobDetail, mockTrigger);
    }

    @DisplayName("스케줄러 예외가 발생하면 RuntimeException으로 감싸서 던진다")
    @Test
    void testRunWithSchedulerException() throws Exception {
        // given
        JobDetail mockJobDetail = mock(JobDetail.class);
        Trigger mockTrigger = mock(Trigger.class);

        when(jobDetailService.buildJobDetail(eq(PaymentScheduleJob.class), any())).thenReturn(mockJobDetail);
        when(triggerService.now()).thenReturn(mockTrigger);
        doThrow(new SchedulerException("Test exception")).when(scheduler).scheduleJob(any(), any());

        // when & then
        assertThatThrownBy(() -> paymentJobRunner.run())
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SchedulerException.class);
    }
}