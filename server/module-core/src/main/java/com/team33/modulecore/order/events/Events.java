package com.team33.modulecore.order.events;

import org.springframework.context.ApplicationEventPublisher;

public class Events {

	private static ApplicationEventPublisher publisher;

	static void setPublisher(ApplicationEventPublisher publisher) {
		Events.publisher = publisher;
	}

	public static void publish(OrderAddedSidEvent event) {
		if (publisher != null) {
			publisher.publishEvent(event);
		}
	}
}
