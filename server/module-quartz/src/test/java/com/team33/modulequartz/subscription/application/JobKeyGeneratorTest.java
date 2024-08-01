package com.team33.modulequartz.subscription.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;

class JobKeyGeneratorTest {

	@DisplayName("jobkey를 생성할 수 있다.")
	@Test
	void jobKey_생성() throws Exception{

		//given //when
		JobKey jobKey = JobKeyGenerator.build(1L, "product");

		//then
		assertThat(jobKey.getName()).isEqualTo("1-product");
		assertThat(jobKey.getGroup()).isEqualTo("1");
	}
}