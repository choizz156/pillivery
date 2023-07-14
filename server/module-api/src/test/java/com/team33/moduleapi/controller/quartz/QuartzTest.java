package com.team33.moduleapi.controller.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import com.team33.moduleapi.controller.ApiTest;
import com.team33.ModuleApiApplication;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("quartztest")
@Import(ModuleApiApplication.class)
class QuartzTest extends ApiTest {

    @Autowired
    private Scheduler scheduler;
    private ZonedDateTime now = ZonedDateTime.now();
    private JobKey jobKey;

    @BeforeEach
    void setUp() {
        jobKey = jobKey("test22", "testest22");
    }

    @DisplayName("trigger 설정")
    @Test
    void test1() throws Exception {

        Trigger trigger = trigger(jobKey);

        assertThat(trigger.getJobKey().getName()).isEqualTo(jobKey.getName());
        assertThat(trigger.getJobKey().getGroup()).isEqualTo(jobKey.getGroup());
        assertThat(trigger.getKey().getName()).isEqualTo(jobKey.getName());
        assertThat(trigger.getKey().getGroup()).isEqualTo(jobKey.getGroup());
        assertThat(trigger.getStartTime()).isEqualTo(Date.from(now.toInstant()));
    }

    @DisplayName("job detail 테스트")
    @Test
    void test2() {

        JobDetail jobDetail = jobDetail(jobKey);

        assertThat(jobDetail.getJobDataMap())
            .containsEntry("orderId", 1L)
            .containsEntry("itemOrder", "itemOrder");

        assertThat(jobDetail.isDurable()).isTrue();
        assertThat(jobDetail.getKey().getName()).isEqualTo(jobKey.getName());
        assertThat(jobDetail.getKey().getGroup()).isEqualTo(jobKey.getGroup());
        assertThat(jobDetail.isDurable()).isTrue();
    }

    @DisplayName("job key 테스트")
    @Test
    void test3() throws Exception {
        assertThat(jobKey.getName()).isEqualTo("test22");
        assertThat(jobKey.getGroup()).isEqualTo("testest22");
    }

    @DisplayName("스케쥴러 테스트")
    @Test
    void test4() throws Exception {
        JobDetail jobDetail = jobDetail(jobKey);
        Trigger trigger = trigger(jobKey);

        scheduler.scheduleJob(jobDetail, trigger);
        JobDetail jobDetail1 = scheduler.getJobDetail(jobKey);
        Trigger trigger1 = scheduler.getTrigger(trigger.getKey());

        assertThat(jobDetail1.isDurable()).isTrue();
        assertThat(jobDetail1.getKey().getName()).isEqualTo(jobKey.getName());
        assertThat(jobDetail1.getKey().getName()).isEqualTo(jobKey.getName());
        assertThat(jobDetail1.getKey().getGroup()).isEqualTo(jobKey.getGroup());

        assertThat(trigger1.getJobKey().getName()).isEqualTo(jobKey.getName());
        assertThat(trigger1.getJobKey().getGroup()).isEqualTo(jobKey.getGroup());
        assertThat(trigger1.getKey().getName()).isEqualTo(jobKey.getName());
        assertThat(trigger1.getKey().getGroup()).isEqualTo(jobKey.getGroup());
        assertThat(trigger1.getStartTime()).isEqualTo(Date.from(now.toInstant()));
    }

    private Trigger trigger(JobKey jobKey) {

        return newTrigger()
            .forJob(jobKey)
            .withSchedule(simpleSchedule()
                .withIntervalInSeconds(5)
            )
            .withIdentity(new TriggerKey(jobKey.getName(), jobKey.getGroup()))
            .startAt(Date.from(now.toInstant()))
            .build();
    }

    private JobDetail jobDetail(JobKey jobKey) {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("orderId", 1L);
        jobDataMap.put("itemOrder", "itemOrder");

        return newJob(Job.class)
            .withIdentity(jobKey.getName(), jobKey.getGroup())
            .storeDurably(true)
            .usingJobData(jobDataMap)
            .build();
    }

}
