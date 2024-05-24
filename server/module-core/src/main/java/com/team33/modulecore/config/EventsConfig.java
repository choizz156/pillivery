package com.team33.modulecore.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventsConfig implements ApplicationEventPublisherAware {

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		Events.setPublisher(applicationEventPublisher);
	}
}
