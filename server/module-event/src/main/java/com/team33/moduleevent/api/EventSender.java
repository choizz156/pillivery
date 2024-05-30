package com.team33.moduleevent.api;

import com.team33.moduleevent.domain.entity.ApiEventSet;

public interface EventSender {
	void send(ApiEventSet event);
}
