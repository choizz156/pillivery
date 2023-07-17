package com.team33.moduleapi.controller.quartz.jobtest;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@Slf4j
@PersistJobDataAfterExecution
public class WrongJob1 implements Job {

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        int retry = (int) context.getJobDetail().getJobDataMap().get("retry");

        log.warn("retry = {}",retry);
        if (retry == 1) {
            log.error("바로 재시작");
            int a = 3 / 2;
            log.info("a = {}", a);
        } else {
            try {
                int b = 3 / 0;
            }catch (ArithmeticException e){
                throw new JobExecutionException();
            }

        }
    }
}
