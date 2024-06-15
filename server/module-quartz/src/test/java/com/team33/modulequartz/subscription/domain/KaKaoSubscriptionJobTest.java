package com.team33.modulequartz.subscription.domain;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.team33.moduleexternalapi.infra.RestTemplateSender;

class KaKaoSubscriptionJobTest {

	@Test
	void job() throws Exception {
		//given
		RestTemplateSender restTemplateSender = mock(RestTemplateSender.class);
		KaKaoSubscriptionJob kaKaoSubscriptionJob = new KaKaoSubscriptionJob(
			restTemplateSender
		);

		JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("orderId", 1L);

		when(jobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);

		//when
		kaKaoSubscriptionJob.execute(jobExecutionContext);

		//then
		verify(jobExecutionContext, times(1)).getMergedJobDataMap();
		verify(restTemplateSender, times(1)).sendToPost(anyString(), anyString(), eq(null), eq(String.class));
	}
}