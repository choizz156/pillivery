package com.team33.modulequartz.subscription.infra;

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
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.team33.modulecore.order.domain.OrderItem;
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
	void job_수행_후_이벤트_발행() throws Exception{
		//given
		OrderItem orderItem = mock(OrderItem.class);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("orderId", 1L);
		jobDataMap.put("orderItem", orderItem);
		jobDataMap.put("retry", 0);

		JobDetail jobDetail = newJob(MockJob.class)
			.withIdentity("testJob")
			.usingJobData(jobDataMap)
			.build();

		Trigger trigger = newTrigger()
			.withIdentity("testTrigger")
			.forJob("testJob")
			.startNow()
			.build();

		ApplicationEventPublisher applicationContext = mock(ApplicationEventPublisher.class);
		doNothing().when(applicationContext).publishEvent(any(PaymentDateUpdatedEvent.class));

		PaymentJobListeners paymentJobListeners = new PaymentJobListeners(applicationContext, null);

		ListenerManager listenerManager = scheduler.getListenerManager();
		listenerManager.addJobListener(paymentJobListeners);


		//when
		scheduler.scheduleJob(jobDetail, trigger);
		scheduler.start();
		Thread.sleep(500);

		//then
		verify(applicationContext, times(1)).publishEvent(any(PaymentDateUpdatedEvent.class));
	}


	public static class MockJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
		}
	}

}