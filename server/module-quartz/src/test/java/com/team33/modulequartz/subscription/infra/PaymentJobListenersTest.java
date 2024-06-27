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

import com.team33.modulequartz.subscription.infra.PaymentJobListeners;

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

	@DisplayName("job이 수행되기 전에 로그를 남길 수 있다.")
	@Test
	void toBeExecuted(CapturedOutput output) throws Exception{
		//given
		PaymentJobListeners paymentJobListeners = new PaymentJobListeners(null, null, null, null, null, null);

		JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);

		JobKey jobKey = new JobKey("testJob","testGroup");

		JobDetail jobDetail = newJob(MockJob.class)
			.withIdentity(jobKey)
			.build();

		when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);

		//when
		paymentJobListeners.jobToBeExecuted(jobExecutionContext);

		//then
		assertThat(output).contains(" 실행될 job의 jobkey = testGroup.testJob");
	}

	@DisplayName("job 수행 전 jobToBeExecuted가 수행된다.")
	@Test
	void job_수행() throws Exception {

		//given
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("orderId", 1L);
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

		PaymentJobListeners listenersTest = mock(PaymentJobListeners.class);
		when(listenersTest.getName()).thenReturn("testListener");

		ListenerManager listenerManager = scheduler.getListenerManager();
		listenerManager.addJobListener(listenersTest);

		//when
		scheduler.scheduleJob(jobDetail, trigger);
		scheduler.start();
		Thread.sleep(500);

		//then
		verify(listenersTest, times(1)).jobToBeExecuted(any(JobExecutionContext.class));
	}

	public static class MockJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
		}
	}

}