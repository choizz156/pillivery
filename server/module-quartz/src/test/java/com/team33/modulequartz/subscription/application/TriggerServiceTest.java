package com.team33.modulequartz.subscription.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Trigger;

import com.team33.modulecore.order.domain.entity.OrderItem;
import com.team33.modulecore.order.domain.SubscriptionInfo;

class TriggerServiceTest {

	@DisplayName("trigger를 생성할 수 있다.")
	@Test
	void trigger_생성() throws Exception{
		//given
		TriggerService triggerService = new TriggerService();

		OrderItem orderItem = OrderItem.builder()
			.subscriptionInfo(SubscriptionInfo.of(true, 30))
			.build();

		//when
		Trigger trigger = triggerService.build(new JobKey("1", "1"), orderItem);

		//then
		assertThat(trigger.getKey().getName()).isEqualTo("1");
		assertThat(trigger.getKey().getGroup()).isEqualTo("1");
		assertThat(trigger.getStartTime()).isEqualTo(Date.from(orderItem.getNextPaymentDay().toInstant()));
		assertThat(trigger.getFireTimeAfter(trigger.getStartTime()))
			.isEqualTo(Date.from(orderItem.getSubscriptionInfo().getNextPaymentDay().plusDays(30).toInstant()));
	}

}