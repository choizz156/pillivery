package com.team33.modulebatch.scheduler.application;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;

@ExtendWith(MockitoExtension.class)
class JobDetailServiceTest {

    @DisplayName("JobDetail을 생성한다")
    @Test
    void testBuildJobDetail() {
        // given
        JobDetailService jobDetailService = new JobDetailService();
        Map<String, Object> params = new HashMap<>();
        params.put("testKey", "testValue");
        
        // when
        JobDetail jobDetail = jobDetailService.buildJobDetail(TestJob.class);
        
        // then
        assertThat(jobDetail).isNotNull();
        assertThat(jobDetail.getJobClass()).isEqualTo(TestJob.class);
        
        JobKey jobKey = jobDetail.getKey();
        assertThat(jobKey).isNotNull();
        assertThat(jobKey.getName()).startsWith("payment-Job-");
        assertThat(jobKey.getGroup()).isEqualTo("batch");
    }
    
    public static class TestJob implements Job {
        @Override
        public void execute(org.quartz.JobExecutionContext context) {

        }
    }
}