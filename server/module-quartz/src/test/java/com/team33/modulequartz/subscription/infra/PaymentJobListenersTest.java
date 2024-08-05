package com.team33.modulequartz.subscription.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulequartz.subscription.domain.PaymentDateUpdatedEvent;

@ExtendWith(OutputCaptureExtension.class)
class PaymentJobListenersTest {

	private Scheduler scheduler;

	@BeforeEach
	void setUp() throws SchedulerException {
		scheduler = StdSchedulerFactory.getDefaultScheduler();
	}

	@AfterEach
	void tearDown() throws SchedulerException {
		scheduler.shutdown();
	}

	@DisplayName("job이 수행된 후 이벤트를 발행할 수 있다.")
	@Test
	void job_수행_후_이벤트_발행() throws Exception {
		//given
		JobDataMap jobDataMap = getJobDataMap();
		JobDetail jobDetail = getJobDetail(jobDataMap);
		Trigger trigger = getTrigger();

		ApplicationEventPublisher applicationContext = getMockApplicationEventPublisher();

		addJobListeners(applicationContext);

		//when
		scheduler.scheduleJob(jobDetail, trigger);
		scheduler.start();
		Thread.sleep(500);

		//then
		verify(applicationContext, times(1)).publishEvent(any(PaymentDateUpdatedEvent.class));
	}

	@DisplayName("job 수행이 한 번 실패시 바로 재시도한다.")
	@Test
	void job_수행_실패(CapturedOutput output) throws Exception {
		// given
		ApplicationEventPublisher mockPublisher = mock(ApplicationEventPublisher.class);
		PaymentJobListeners listeners = new PaymentJobListeners(mockPublisher);

		JobExecutionContext mockContext = mock(JobExecutionContext.class);
		JobExecutionException mockException = mock(JobExecutionException.class);

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("retry", 0);
		when(mockContext.getJobDetail()).thenReturn(mock(JobDetail.class));
		when(mockContext.getJobDetail().getJobDataMap()).thenReturn(jobDataMap);

		// when
		listeners.jobWasExecuted(mockContext, mockException);

		// then
		verify(mockException).setRefireImmediately(true);
		verify(mockPublisher, times(1)).publishEvent(any(PaymentDateUpdatedEvent.class));

		assertThat(jobDataMap.getInt("retry")).isEqualTo(1);
		assertThat(output).contains("job exception = " + mockException.getMessage());
	}

	@DisplayName("job 수행이 두 번 실패시 스케쥴이 취소된다.")
	@Test
	void job_수행_실패_2(CapturedOutput output) throws Exception {
		// given
		ApplicationEventPublisher mockPublisher = mock(ApplicationEventPublisher.class);
		PaymentJobListeners listeners = new PaymentJobListeners(mockPublisher);

		JobExecutionContext mockContext = mock(JobExecutionContext.class);
		JobExecutionException mockException = mock(JobExecutionException.class);

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("retry", 2);
		JobDetail jobDetail = mock(JobDetail.class);
		when(jobDetail.getKey()).thenReturn(new JobKey("testKey"));
		when(mockContext.getJobDetail()).thenReturn(jobDetail);
		when(mockContext.getJobDetail().getJobDataMap()).thenReturn(jobDataMap);

		// when then
		assertThatThrownBy(
			() -> listeners.jobWasExecuted(mockContext, mockException)
		)
			.isInstanceOf(BusinessLogicException.class);

		verify(mockException).setUnscheduleAllTriggers(true);
		verify(mockPublisher, times(0)).publishEvent(any(PaymentDateUpdatedEvent.class));

		assertThat(jobDataMap.getInt("retry")).isEqualTo(2);
		assertThat(output).contains(" job 예외로 인한 스케쥴 취소 = ");
	}

	private ApplicationEventPublisher getMockApplicationEventPublisher() {
		ApplicationEventPublisher applicationContext = mock(ApplicationEventPublisher.class);
		doNothing().when(applicationContext).publishEvent(any(PaymentDateUpdatedEvent.class));
		return applicationContext;
	}

	private void addJobListeners(ApplicationEventPublisher applicationContext) throws SchedulerException {
		PaymentJobListeners paymentJobListeners = new PaymentJobListeners(applicationContext);
		ListenerManager listenerManager = scheduler.getListenerManager();
		listenerManager.addJobListener(paymentJobListeners);
	}

	private Trigger getTrigger() {
		return newTrigger()
			.withIdentity("testTrigger")
			.forJob("testJob")
			.startNow()
			.build();
	}

	private JobDetail getJobDetail(JobDataMap jobDataMap) {
		return newJob(MockJob.class)
			.withIdentity("testJob")
			.usingJobData(jobDataMap)
			.build();
	}

	private JobDataMap getJobDataMap() {
		OrderItem orderItem = mock(OrderItem.class);
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("orderId", 1L);
		jobDataMap.put("orderItem", orderItem);
		jobDataMap.put("retry", 0);
		return jobDataMap;
	}

	public static class MockJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
		}
	}

}