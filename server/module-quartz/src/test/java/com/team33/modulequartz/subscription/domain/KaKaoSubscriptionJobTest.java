package com.team33.modulequartz.subscription.domain;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.team33.moduleexternalapi.infra.WebClientSender;

class KaKaoSubscriptionJobTest {

	@Test
	void job() throws Exception {
		//given
		WebClientSender WebClientSender = mock(WebClientSender.class);
		KaKaoSubscriptionJob kaKaoSubscriptionJob = new KaKaoSubscriptionJob(
			WebClientSender
		);

		JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("orderId", 1L);

		when(jobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);

		//when
		kaKaoSubscriptionJob.execute(jobExecutionContext);

		//then
		verify(jobExecutionContext, times(1)).getMergedJobDataMap();
		verify(WebClientSender, times(1)).sendToPost(eq(null), anyString(), eq(null), eq(String.class));
	}
}