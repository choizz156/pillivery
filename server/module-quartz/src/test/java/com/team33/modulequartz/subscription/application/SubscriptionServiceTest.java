package com.team33.modulequartz.subscription.application;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.core.ListenerManagerImpl;

import com.team33.modulecore.common.OrderFindHelper;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.SubscriptionInfo;
import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulequartz.subscription.FixtureMonkeyFactory;

class SubscriptionServiceTest {

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
				null,
				null,
				null,
				orderFindHelper
				);

		//when
		subscriptionService.applySchedule(1L);

		//then
		verify(orderFindHelper, times(1)).findOrder(anyLong());
		verify(scheduler, times(1)).scheduleJob(any(), any());
		verify(triggerService, times(1)).build(any(), any());
		verify(jobDetailService, times(1)).build(any(JobKey.class), anyLong());
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

}