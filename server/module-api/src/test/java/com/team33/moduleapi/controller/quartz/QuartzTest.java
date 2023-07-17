package com.team33.moduleapi.controller.quartz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import com.team33.ModuleApiApplication;
import com.team33.moduleapi.controller.ApiTest;
import com.team33.moduleapi.controller.quartz.jobtest.TestJob1;
import com.team33.moduleapi.controller.quartz.jobtest.TestJobListener;
import com.team33.moduleapi.controller.quartz.jobtest.TestTriggerListener;
import com.team33.moduleapi.controller.quartz.jobtest.WrongJob1;
import com.team33.moduleapi.controller.quartz.jobtest.WrongJob2;
import com.team33.modulequartz.subscription.trigger.TriggerService;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(ModuleApiApplication.class)
class QuartzTest extends ApiTest {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TriggerService triggerService;

    private ZonedDateTime now = ZonedDateTime.now();
    private JobKey jobKey;

    @BeforeEach
    void setUp() throws Exception {
        ListenerManager listenerManager = scheduler.getListenerManager();
        listenerManager.addJobListener(new TestJobListener(triggerService));
        listenerManager.addTriggerListener(new TestTriggerListener());
        jobKey = jobKey("test1", "test1");
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
        assertThat(jobKey.getName()).isEqualTo("test1");
        assertThat(jobKey.getGroup()).isEqualTo("test1");
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

    @DisplayName("job이 설정된 후 job을 조회 가능하다.")
    @Test
    void test5() throws Exception {
        Trigger trigger = trigger(jobKey);
        scheduler.scheduleJob(jobDetail(jobKey), trigger);

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

        ListenerManager listenerManager = scheduler.getListenerManager();
        listenerManager.addJobListener(new TestJobListener(triggerService));

        assertThat(jobDetail.getKey().getName()).isEqualTo("test1");
        assertThat(jobDetail.getKey().getGroup()).isEqualTo("test1");
        assertThat(listenerManager.getJobListeners()).isNotNull();
    }

    @DisplayName("job이 설정된 후 trigger를 조회 가능하다.")
    @Test
    void test6() throws Exception {
        Trigger trigger = trigger(jobKey);
        scheduler.scheduleJob(jobDetail(jobKey), trigger);

        Trigger trigger1 = scheduler.getTrigger(trigger.getKey());

        ListenerManager listenerManager = scheduler.getListenerManager();
        listenerManager.addJobListener(new TestJobListener(triggerService));
        listenerManager.addTriggerListener(new TestTriggerListener());

        assertThat(trigger1.getKey().getName()).isEqualTo("test1");
        assertThat(trigger1.getKey().getGroup()).isEqualTo("test1");
        assertThat(listenerManager.getTriggerListeners()).isNotNull();
    }

    @DisplayName("첫번째 job 실패시 바로 재실행 한다.")
    @Test
    void test7() throws Exception {
        Trigger trigger = trigger(jobKey);

        ListenerManager listenerManager = scheduler.getListenerManager();
        listenerManager.addJobListener(new TestJobListener(triggerService));
        scheduler.scheduleJob(wrongJob1(jobKey), trigger);

        assertThat(scheduler.getJobDetail(jobKey).isPersistJobDataAfterExecution()).isTrue();
        int retry = (int) scheduler.getJobDetail(jobKey).getJobDataMap().get("retry");
        assertThat(retry).isEqualTo(1);
    }


    @DisplayName("두번째 job 실패시 일정 시간이 지난 후 재실행한다.")
    @Test
    void test8() throws Exception {
        Trigger trigger = trigger(jobKey);
        scheduler.scheduleJob(wrongJob2(jobKey), trigger);

        Thread.sleep(7000);

        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
        int retry = (int) scheduler.getJobDetail(jobKey).getJobDataMap().get("retry");
        assertThat(triggerKeys).contains(TriggerKey.triggerKey("test2", "test2"));
        assertThat(retry).isEqualTo(2);
    }

    private Trigger trigger(JobKey jobKey) {

        return newTrigger()
            .forJob(jobKey)
            .withSchedule(simpleSchedule()
                .withIntervalInSeconds(1)
                .withRepeatCount(11)
            )
            .withIdentity(new TriggerKey(jobKey.getName(), jobKey.getGroup()))
            .startNow()
            .build();
    }

    private JobDetail jobDetail(JobKey jobKey) {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("id", 1L);
        jobDataMap.put("retry", 0);

        return newJob(TestJob1.class)
            .withIdentity(jobKey.getName(), jobKey.getGroup())
            .storeDurably(true)
            .usingJobData(jobDataMap)
            .build();
    }

    private JobDetail wrongJob1(JobKey jobKey) {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("id", 1L);
        jobDataMap.put("retry", 0);

        return newJob(WrongJob1.class)
            .withIdentity(jobKey.getName(), jobKey.getGroup())
            .storeDurably(true)
            .usingJobData(jobDataMap)
            .build();
    }

    private JobDetail wrongJob2(JobKey jobKey) {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("id", 1L);
        jobDataMap.put("retry", 0);

        return newJob(WrongJob2.class)
            .withIdentity(jobKey.getName(), jobKey.getGroup())
            .storeDurably(true)
            .usingJobData(jobDataMap)
            .build();
    }
}
