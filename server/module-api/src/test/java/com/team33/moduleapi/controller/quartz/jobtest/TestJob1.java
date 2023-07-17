package com.team33.moduleapi.controller.quartz.jobtest;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@Slf4j
@PersistJobDataAfterExecution
public class TestJob1 implements Job {

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        log.info("=====Test Job=====");
        int retry = (int) context.getMergedJobDataMap().get("retry");
        log.info("retry = {}", retry);
    }
}
