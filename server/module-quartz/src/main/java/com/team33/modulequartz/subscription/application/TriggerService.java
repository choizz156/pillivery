// package com.team33.modulequartz.subscription.application;
//
// import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
// import static org.quartz.DateBuilder.IntervalUnit.MINUTE;
// import static org.quartz.DateBuilder.futureDate;
// import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
// import static org.quartz.TriggerBuilder.newTrigger;
//
// import com.team33.modulecore.order.domain.OrderItem;
// import java.util.Date;
// import lombok.extern.slf4j.Slf4j;
// import org.quartz.JobKey;
// import org.quartz.Trigger;
// import org.quartz.TriggerKey;
// import org.springframework.stereotype.Component;
//
// @Slf4j
// @Component
// public class TriggerService {
//
//     public Trigger build(JobKey jobKey, OrderItem orderItem) {
//         log.info("trigger 설정");
//         return newTrigger()
//             .forJob(jobKey)
//             .withSchedule(
//                 calendarIntervalSchedule().withIntervalInDays(orderItem.getPeriod())
//             )
//             .withIdentity(new TriggerKey(jobKey.getName(), jobKey.getGroup()))
//             .startAt(Date.from(orderItem.getNextDelivery().toInstant()))
//             .build();
//     }
//
//     public Trigger retryTrigger() {
//         log.info("retry trigger 설정");
//         return newTrigger()
//             .withSchedule(simpleSchedule()
//                 .withIntervalInHours(24)
//                 .withRepeatCount(3)
//             )
//             .startAt(futureDate(10, MINUTE))
//             .withIdentity(new TriggerKey("retry"))
//             .build();
//     }
// }
