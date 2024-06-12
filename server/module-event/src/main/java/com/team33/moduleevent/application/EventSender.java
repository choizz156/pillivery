package com.team33.moduleevent.application;

import com.team33.moduleevent.domain.entity.ApiEvent;

public interface EventSender {
	void send(ApiEvent event);
}
