package com.team33.modulequartz.subscription.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.core.ListenerManagerImpl;
import org.quartz.impl.StdSchedulerFactory;

import com.team33.modulecore.core.common.OrderFindHelper;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.domain.SubscriptionInfo;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.repository.OrderQueryRepository;
import com.team33.modulequartz.subscription.FixtureMonkeyFactory;

class SubscriptionServiceTest {

	private Scheduler scheduler;

	@BeforeEach
	void setUp() throws SchedulerException {
		scheduler = StdSchedulerFactory.getDefaultScheduler();
	}

	@AfterEach
	void tearDown() throws SchedulerException {
		scheduler.shutdown();
	}

	@DisplayName("스케쥴을 등록할 수 있다.")
	@Test
	void 스케쥴_등록() throws Exception {
		//given
		Scheduler scheduler = mock(Scheduler.class);
		TriggerService triggerService = mock(TriggerService.class);
		JobDetailService jobDetailService = mock(JobDetailService.class);
		OrderFindHelper orderFindHelper = mock(OrderFindHelper.class);

		Order order = FixtureMonkeyFactory.get()
			.giveMeBuilder(Order.class)
			.set("id", 1L)
			.set("orderItems", List.of(getMockOrderItems()))
			.set("totalQuantity", 9)
			.set("paymentCode.sid", "sid")
			.sample();

		when(orderFindHelper.findOrder(anyLong())).thenReturn(order);
		when(scheduler.getListenerManager()).thenReturn(new ListenerManagerImpl());

		SubscriptionService subscriptionService =
			new SubscriptionService(
				scheduler,
				triggerService,
				jobDetailService,
				null,
				orderFindHelper,
				null
			);

		//when
		subscriptionService.applySchedule(1L);

		//then
		verify(orderFindHelper, times(1)).findOrder(anyLong());
		verify(scheduler, times(1)).scheduleJob(any(), any());
		verify(triggerService, times(1)).build(any(), any());
		verify(jobDetailService, times(1)).build(any(JobKey.class), anyLong(), anyLong());
	}

	@DisplayName("스케쥴 주기를 변경할 수 있다.")
	@Test
	void 스케쥴_주기_변경() throws Exception {
		//given
		ZonedDateTime now = ZonedDateTime.now();
		스케쥴_설정(now);

		OrderQueryRepository orderQueryRepository = mock(OrderQueryRepository.class);
		OrderItem mockOrderItem = getMockOrderItems();
		mockOrderItem.addPaymentDay(now);
		when(orderQueryRepository.findSubscriptionOrderItemBy(anyLong())).thenReturn(mockOrderItem);

		SubscriptionService subscriptionService = new SubscriptionService(
			scheduler,
			new TriggerService(),
			null,
			new OrderItemService(null, null, null, orderQueryRepository),
			null,
			null
		);

		//when
		subscriptionService.changePeriod(1L, 60, 1L);

		//then
		Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey("1-mockItem", "1"));

		LocalDate nextFireTime = trigger.getNextFireTime().toInstant().atZone(now.getZone()).toLocalDate();
		LocalDate startTime = trigger.getStartTime().toInstant().atZone(now.getZone()).toLocalDate();

		assertThat(mockOrderItem.getPeriod()).isEqualTo(60);
		assertThat(nextFireTime).isEqualTo(now.plusDays(30).toLocalDate());
		assertThat(startTime).isEqualTo(now.plusDays(30).toLocalDate());
	}

	@DisplayName("스케쥴을 취소할 수 있다.")
	@Test
	void 스케쥴_취소() throws Exception {
		//given
		ZonedDateTime now = ZonedDateTime.now();
		스케쥴_설정(now);

		OrderQueryRepository orderQueryRepository = mock(OrderQueryRepository.class);
		OrderItem mockOrderItem = getMockOrderItems();
		when(orderQueryRepository.findSubscriptionOrderItemBy(anyLong())).thenReturn(mockOrderItem);

		SubscriptionService subscriptionService = new SubscriptionService(
			scheduler,
			new TriggerService(),
			null,
			new OrderItemService(null, null, null, orderQueryRepository),
			null,
			null
		);

		//when
		subscriptionService.cancelScheduler(1L, 1L);

		//then
		boolean result = scheduler.checkExists(TriggerKey.triggerKey("1-mockItem", "1"));
		assertThat(result).isFalse();
	}

	private void 스케쥴_설정(ZonedDateTime now) throws SchedulerException {
		JobDataMap jobDataMap = getJobDataMap();
		JobDetail jobDetail = getJobDetail(jobDataMap);
		Trigger trigger = getTrigger(now);

		scheduler.scheduleJob(jobDetail, trigger);
		scheduler.start();
	}

	private OrderItem getMockOrderItems() {
		return FixtureMonkeyFactory.get().giveMeBuilder(OrderItem.class)
			.set("item", getMockItem())
			.set("quantity", 3)
			.set("subscriptionInfo", SubscriptionInfo.of(true, 30))
			.set("item.information.price.realPrice", 1000)
			.set("item.information.price.discountPrice", 500)
			.sample();
	}

	private Item getMockItem() {
		return FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("statistics.sales", 1)
			.set("information.price.discountRate", 0D)
			.set("information.price.realPrice", 3000)
			.set("information.price.originPrice", 3000)
			.set("information.productName", "mockItem")
			.sample();
	}

	private JobDetail getJobDetail(JobDataMap jobDataMap) {
		return newJob(MockJob.class)
			.withIdentity("1-mockItem", "1")
			.usingJobData(jobDataMap)
			.build();
	}

	private JobDataMap getJobDataMap() {
		OrderItem orderItem = mock(OrderItem.class);
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("orderId", 1L);
		jobDataMap.put("orderItem", orderItem);
		jobDataMap.put("retry", 0);
		return jobDataMap;
	}

	private Trigger getTrigger(ZonedDateTime now) {
		return newTrigger()
			.withIdentity("1-mockItem", "1")
			.forJob("1-mockItem", "1")
			.withSchedule(calendarIntervalSchedule().withIntervalInDays(30))
			.startAt(Date.from(now.toInstant()))
			.build();
	}

	public static class MockJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
		}
	}
}