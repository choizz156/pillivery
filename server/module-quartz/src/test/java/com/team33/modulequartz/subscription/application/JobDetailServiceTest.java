package com.team33.modulequartz.subscription.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;

class JobDetailServiceTest {

	@DisplayName("jobDetail을 생성할 수 있다.")
	@Test
	void jobDetail_생성() throws Exception {
		//given
		JobKey jobKey = JobKeyGenerator.build(1L, "product");

		//when
		JobDetailService jobDetailService = new JobDetailService();
		JobDetail jobDetail = jobDetailService.build(jobKey, 1L,1L);

		//then
		assertThat(jobDetail.getKey().getName()).isEqualTo("1-product");
		assertThat(jobDetail.getKey().getGroup()).isEqualTo("1");
		assertThat(jobDetail.getJobDataMap()).hasSize(2)
			.extracting("orderId", "retry")
			.contains(1L, 0);
	}
}