package com.team33.modulecore.eventstore.api;

import com.team33.modulecore.eventstore.domain.entity.ApiEventSet;

public interface EventSender {
	void send(ApiEventSet event);
}
